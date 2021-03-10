package com.redhat.bobbycar.carsim;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.LongStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.net.ssl.SSLException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.redhat.bobbycar.carsim.cars.Car;
import com.redhat.bobbycar.carsim.cars.EngineException;
import com.redhat.bobbycar.carsim.cars.EngineMetrics;
import com.redhat.bobbycar.carsim.cars.JsonEngineConfiguration;
import com.redhat.bobbycar.carsim.cars.TimedEngine;
import com.redhat.bobbycar.carsim.cars.events.CarMetricsEvent;
import com.redhat.bobbycar.carsim.cars.events.CarMetricsEventPublisher;
import com.redhat.bobbycar.carsim.clients.DataGridService;
import com.redhat.bobbycar.carsim.clients.KafkaService;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarEvent;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarPosition;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarRecord;
import com.redhat.bobbycar.carsim.consumer.ZoneChangeConsumer;
import com.redhat.bobbycar.carsim.consumer.ZoneChangeListener;
import com.redhat.bobbycar.carsim.data.DriverDao;
import com.redhat.bobbycar.carsim.drivers.Driver;
import com.redhat.bobbycar.carsim.drivers.DriverMetrics;
import com.redhat.bobbycar.carsim.drivers.TimedDrivingStrategy;
import com.redhat.bobbycar.carsim.drivers.TimedDrivingStrategyMetrics;
import com.redhat.bobbycar.carsim.gpx.GpxReader;
import com.redhat.bobbycar.carsim.routes.CompositeRouteSelector;
import com.redhat.bobbycar.carsim.routes.FileBasedRouteSelector;
import com.redhat.bobbycar.carsim.routes.OsmRouteSelector;
import com.redhat.bobbycar.carsim.routes.Route;
import com.redhat.bobbycar.carsim.routes.RouteSelectionStrategy;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CarSimulatorApp {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CarSimulatorApp.class);
	// Config Properties
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.cars", defaultValue = "1")
	int cars;
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.route")
	String pathToRoutes;
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.route.remote", defaultValue = "")
	Optional<String[]> remoteRoutes;
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.delay", defaultValue = "0")
	int delay;
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.factor", defaultValue = "1.0")
	double factor;
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.repeat", defaultValue = "false")
	boolean repeat;
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.kafka.apiKey")
	Optional<String> apiKey;
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.mockHttp", defaultValue = "false")
	boolean mockHttp;
	
	// CDI Beans
	@Inject
    DriverDao driverDao;
	@Inject
	GpxReader gpxReader;
	@Inject
	MetricRegistry registry;
	@Inject
	OsmRouteSelector osmRouteSelector;
	@Inject
	TimedDrivingStrategyMetrics timedDrivingStrategyMetrics;
	@Inject
	DriverMetrics driverMetrics;
	@Inject
	ZoneChangeConsumer zoneChangeConsumer;
	@Inject
	CarMetricsEventPublisher carMetricsPublisher;
	// Rest Clients
	@Inject
    @RestClient
    KafkaService kafkaService;
	@RestClient
    @Inject
    DataGridService dataGridService;
	
	// Attributes
    private RouteSelectionStrategy routeSelectionStrategy;
	private final Map<UUID, CompletableFuture<Void>> futures;
	private WireMockServer wireMockServer;

	public CarSimulatorApp() throws JAXBException {
		futures = new HashMap<>();
	}
	
	void onStart(@Observes StartupEvent ev) {  
		mockHttp();
		ThreadPoolExecutor carPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cars);
		ThreadPoolExecutor enginePoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cars);
        LOGGER.info("The application is starting... ");
        LOGGER.info("Reading routes from {}", pathToRoutes);
        LongStream.range(0, cars).forEach(c -> {
        	try {
	        	UUID id = UUID.randomUUID();
		    	Route route = getRouteSelectionStrategy().selectRoute();
		    	EngineMetrics engineMetrics = new EngineMetrics(registry, id, route.getName());
		    	TimedEngine engine = TimedEngine.builder().withSpeedVariationInKmH(5).withStartingPoint(route.getPoints().findFirst().orElse(null))
							.withConfig(new JsonEngineConfiguration()).withMetrics(engineMetrics).build();
		    	Car car = Car.builder().withModel("M3 Coupe").withManufacturer("BMW")
						.withEngine(engine).withDriverId(id)
						.build();
		    	engine.registerEventListener(e -> carMetricsPublisher.publish(CarMetricsEvent.create(car, e.getEngineData())));
				zoneChangeConsumer.registerZoneChangeListener(onZoneChange(id));
				TimedDrivingStrategy strategy = TimedDrivingStrategy.builder()
		    			.withFactor(factor)
		    			.withCar(car)
		    			.withMetrics(timedDrivingStrategyMetrics)
		    			.build();
		        Driver driver = Driver.builder()
		        		.withRoute(route)
		        		.withDrivingStrategy(strategy)
		        		.withRepeat(repeat)
		        		.withMetrics(driverMetrics)
		        		.withStartDelay(delay * (c + 1))
		        		.withId(id)
		        		.build();
		        onCarEvent(driver);
		        futures.put(id, CompletableFuture.runAsync(driver, carPoolExecutor));
		        car.start(enginePoolExecutor);
		        driverDao.create(driver.getId(), driver);
		        if (repeat && LOGGER.isInfoEnabled()) {
		        	LOGGER.info("Will repeat route when finished");
		        }
			} catch (FileNotFoundException e) {
				throw new EngineException("Engine configuration could not be loaded", e);
			}
        });
    }

	private void mockHttp() {
		if (mockHttp) {
			wireMockServer = new WireMockServer(8081);
			wireMockServer.start(); 
			wireMockServer.stubFor(post(urlMatching("/topics/bobbycar-gps")).willReturn(
					aResponse().withHeader("Content-Type", "application/json").withBody("")));
			
		}
	}
	
	void onStop(@Observes ShutdownEvent ev) {               
        LOGGER.info("The application is stopping...");
        if (mockHttp) {
			wireMockServer.stop(); 
		}
    }
	
	synchronized RouteSelectionStrategy getRouteSelectionStrategy() {
		if (routeSelectionStrategy == null) {
			List<RouteSelectionStrategy> strategies = new ArrayList<>();
			strategies.add(new FileBasedRouteSelector(gpxReader, pathToRoutes));
			addRemoteSelectionStrategies(strategies);
			routeSelectionStrategy = new CompositeRouteSelector(strategies);
		}
		return routeSelectionStrategy;
	}

	private void addRemoteSelectionStrategies(List<RouteSelectionStrategy> strategies) {
		remoteRoutes.ifPresent(r -> {
			Set<String> remoteStrategies = new HashSet<>(Arrays.asList(r));
			if (remoteStrategies.contains("osm")) {
				strategies.add(osmRouteSelector);
				 LOGGER.info("Adding osm as remote route source");
			}
		});
		
	}

	private ZoneChangeListener onZoneChange(UUID driverId) {
		return evt -> {
			if (driverId.toString().equals(evt.getCarId())) {
				retrieveZoneData(evt.getNextZoneId());
			}
		};
	}

	private void retrieveZoneData(String zoneId) {
		if (zoneId != null) {
			LOGGER.info("Retrieved zone data {}",
					dataGridService.getZoneData("", MediaType.APPLICATION_JSON, zoneId));
		} else {
			LOGGER.info("Left zone");
		}
	}

	private void onCarEvent(Driver driver) {
		driver.registerCarEventListener(evt -> publishToKafka(driver, evt));
	}

	private void publishToKafka(Driver driver, CarEvent evt) {
		List<KafkaCarRecord> records = new ArrayList<>();
		 records.add(new KafkaCarRecord(driver.getId().toString(), new KafkaCarPosition(evt.getLatitude().doubleValue(), evt.getLongitude().doubleValue(), evt.getElevation().doubleValue(), driver.getId().toString(), evt.getTime().orElse(null))));
		 KafkaCarEvent event = new KafkaCarEvent(records);
		 try {
			 kafkaService.publishCarEvent(apiKey.orElse(null), event);
		 } catch(Exception e) {
			 LOGGER.error("Error publishing car event to kafka", e);
			 if (e.getCause() instanceof SSLException) {
				 LOGGER.error("Cannot recover from SSL error. Shutting down.");
				 throw e;
				 //TODO Shutdown Service or set health check to unhealthy state
			 }
		 }
	}

	
}

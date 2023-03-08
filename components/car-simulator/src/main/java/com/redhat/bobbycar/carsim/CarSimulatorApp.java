package com.redhat.bobbycar.carsim;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
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
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import com.redhat.bobbycar.carsim.cloud.Provisioner;
import com.redhat.bobbycar.carsim.cloud.Telemetry;
import com.redhat.bobbycar.carsim.consumer.OTAConsumer;
import com.redhat.bobbycar.carsim.data.CarDao;
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
import com.redhat.bobbycar.carsim.clients.model.KafkaCarPosition;
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
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.cars", defaultValue = "10")
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
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.mockHttp", defaultValue = "false")
	boolean mockHttp;
	
	// CDI Beans
	@Inject
    DriverDao driverDao;
	@Inject
	CarDao carDao;
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
	OTAConsumer otaConsumer;
	@Inject
	CarMetricsEventPublisher carMetricsPublisher;
	@RestClient
    @Inject
    DataGridService dataGridService;
	@Inject
	Provisioner provisioner;
	@Inject
	Telemetry telemetry;

	
	// Attributes
    private RouteSelectionStrategy routeSelectionStrategy;
	private final Map<String, CompletableFuture<Void>> futures;
	private WireMockServer wireMockServer;

	public CarSimulatorApp() throws JAXBException {
		futures = new HashMap<>();
	}
	
	void onStart(@Observes StartupEvent ev) {  
		mockHttp();
		ThreadPoolExecutor carPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cars);
		ThreadPoolExecutor enginePoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cars);
        LOGGER.info("The application is starting... ");
		LOGGER.info("Using telemetry implementation {}", telemetry);
        LOGGER.info("Reading routes from {}", pathToRoutes);
        LongStream.range(0, cars).forEach(c -> {
        	try {
				String id = provisioner.provisionDevice(c);
		    	Route route = getRouteSelectionStrategy().selectRoute();
		    	EngineMetrics engineMetrics = new EngineMetrics(registry, id, route.getName());
		    	TimedEngine engine = TimedEngine.builder().withSpeedVariationInKmH(5).withStartingPoint(route.getPoints().findFirst().orElse(null))
							.withConfig(new JsonEngineConfiguration()).withMetrics(engineMetrics).build();
		    	Car car = Car.builder().withModel("M3 Coupe").withManufacturer("BMW")
						.withEngine(engine)
						.withDriverId(id)
						.withVin(id)
						.build();
				otaConsumer.registerOTAListener(id, car);
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
				LOGGER.info("Created driver with id: "+id);
		        onCarEvent(driver);
		        futures.put(id, CompletableFuture.runAsync(driver, carPoolExecutor));
		        car.start(enginePoolExecutor);
		        driverDao.create(driver.getId(), driver);
				carDao.create(car.getDriverId(), car);
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

	private ZoneChangeListener onZoneChange(String driverId) {
		return evt -> {
			if (driverId.equals(evt.getCarId())) {
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
		driver.registerCarEventListener(evt -> publishCarEvent(driver, evt));
	}

	private void publishCarEvent(Driver driver, CarEvent evt) {
		var event = new KafkaCarPosition(
				evt.getLatitude().doubleValue(),
				evt.getLongitude().doubleValue(),
				evt.getElevation().doubleValue(),
				driver.getId(),
				evt.getTime().orElseGet(ZonedDateTime::now),
				evt.getVin());
		LOGGER.debug("Publish car metrics: {} -> {}", driver, event);
		this.telemetry.publishCarEvent(driver, event);
	}
}

package com.redhat.bobbycar.carsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.LongStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.net.ssl.SSLException;
import javax.xml.bind.JAXBException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.clients.KafkaService;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarEvent;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarPosition;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarRecord;
import com.redhat.bobbycar.carsim.data.DriverDao;
import com.redhat.bobbycar.carsim.drivers.Driver;
import com.redhat.bobbycar.carsim.drivers.TimedDrivingStrategy;
import com.redhat.bobbycar.carsim.gpx.GpxReader;
import com.redhat.bobbycar.carsim.routes.RandomRouteSeclector;
import com.redhat.bobbycar.carsim.routes.Route;
import com.redhat.bobbycar.carsim.routes.RouteSelectionStrategy;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CarSimulatorApp {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CarSimulatorApp.class);
	
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.cars", defaultValue = "1")
	int cars;
	
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.route")
	String pathToRoutes;
	
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.factor", defaultValue = "1.0")
	double factor;
	
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.repeat", defaultValue = "false")
	boolean repeat;
	
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.kafka.apiKey")
	Optional<String> apiKey;
	
	@Inject
    @RestClient
    KafkaService kafkaService;
	
	@Inject
    DriverDao driverDao;
	
	@Inject
	GpxReader gpxReader;
	
    private RouteSelectionStrategy routeSelectionStrategy;
	
	private final Map<UUID, CompletableFuture<Void>> futures;
	
	public CarSimulatorApp() throws JAXBException {
		futures = new HashMap<>();
	}
	
	synchronized RouteSelectionStrategy getRouteSelectionStrategy() {
		if (routeSelectionStrategy == null) {
			routeSelectionStrategy = new RandomRouteSeclector(gpxReader, pathToRoutes);
		}
		return routeSelectionStrategy;
	}
	
	void onStart(@Observes StartupEvent ev) {  
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cars);
        LOGGER.info("The application is starting... ");
        LOGGER.info("Reading routes from {}", pathToRoutes);
        LongStream.range(0, cars).forEach(c -> {
        	try {
	        	Route route = getRouteSelectionStrategy().selectRoute();
	        	TimedDrivingStrategy strategy = TimedDrivingStrategy.builder().withFactor(factor).build();
	            Driver driver = Driver.builder().withRoute(route).withDrivingStrategy(strategy).withRepeat(repeat).build();
	            driver.registerCarEventListener(evt -> {
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
	                	 }
	                 }
	            });
	            futures.put(driver.getId(), CompletableFuture.runAsync(driver, executor));
	            driverDao.create(driver.getId(), driver);
	            if (repeat && LOGGER.isInfoEnabled()) {
	            	LOGGER.info("Will repeat route when finished");
	            }
        	} catch (IOException | JAXBException e) {
        		LOGGER.error("Error reading route", e);
        	}
        });
    }

	void onStop(@Observes ShutdownEvent ev) {               
        LOGGER.info("The application is stopping...");
    }
}

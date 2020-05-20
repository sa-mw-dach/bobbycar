package com.redhat.bobbycar.carsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.LongStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.clients.KafkaService;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarEvent;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarPosition;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarRecord;
import com.redhat.bobbycar.carsim.gpx.GpxReader;

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
	
	@Inject
    @RestClient
    KafkaService kafkaService;
	
	private final Map<UUID, Driver> drivers = new HashMap<>();
	private final Map<UUID, CompletableFuture<Void>> futures = new HashMap<>();
	private GpxReader reader;
	private Random random = new Random();
	private ThreadPoolExecutor executor;
	
	public CarSimulatorApp() throws JAXBException {
		reader = new GpxReader();
		
	}
	
	void onStart(@Observes StartupEvent ev) throws JAXBException {  
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cars);
        LOGGER.info("The application is starting... ");
        LOGGER.info("Reading routes from {}", pathToRoutes);
        LongStream.range(0, cars).forEach(c -> {
        	try {
	        	Route route = pickRoute();
	        	TimedDrivingStrategy strategy = TimedDrivingStrategy.builder().withFactor(factor).build();
	            Driver driver = Driver.builder().withRoute(route).withDrivingStrategy(strategy).withRepeat(repeat).build();
	            driver.registerCarEventListener(evt -> {
	            	 List<KafkaCarRecord> records = new ArrayList<>();
	                 records.add(new KafkaCarRecord(driver.getId().toString(), new KafkaCarPosition(evt.getLatitude().doubleValue(), evt.getLongitude().doubleValue(), evt.getElevation().doubleValue(), driver.getId().toString(), evt.getTime().orElse(null))));
	                 KafkaCarEvent event = new KafkaCarEvent(records);
	                 try {
	                	 kafkaService.publishCarEvent(event);
	                	 
	                 } catch(Exception e) {
	                	 LOGGER.error("Error publishing car event to kafka", e);
	                 }
	            });
	            futures.put(driver.getId(), CompletableFuture.runAsync(driver, executor));
	            drivers.put(driver.getId(), driver);
	            if (repeat && LOGGER.isInfoEnabled()) {
	            	LOGGER.info("Repeating route");
	            }
        	} catch (IOException | JAXBException e) {
        		LOGGER.error("Error reading route", e);
        	}
        });
    }

	private Route pickRoute() throws JAXBException, IOException {
		File file = new File(pathToRoutes);
        Route route;
        if (file.isDirectory()) {
        	File[] gpxFiles = file.listFiles(f -> "gpx".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
        	route = reader.readGpx(gpxFiles[random.nextInt(gpxFiles.length)]);
        } else {
        	route = reader.readGpx(file);
        }
		return route;
	}
	
	public Collection<Driver> getDrivers() {
		return drivers.values();
	}
	
	public Driver getDriver(UUID id) {
		return drivers.get(id);
	}

	void onStop(@Observes ShutdownEvent ev) {               
        LOGGER.info("The application is stopping...");
    }
}

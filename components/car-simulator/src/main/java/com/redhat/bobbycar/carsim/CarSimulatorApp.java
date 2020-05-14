package com.redhat.bobbycar.carsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

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
	long cars;
	
	@ConfigProperty(name = "com.redhat.bobbycar.carsim.route")
	String pathToRoutFile;
	
	@Inject
    @RestClient
    KafkaService kafkaService;
	
	private List<Driver> drivers = new ArrayList<>();
	private GpxReader reader;
	
	public CarSimulatorApp() throws JAXBException {
		reader = new GpxReader();
	}
	
	void onStart(@Observes StartupEvent ev) throws FileNotFoundException, JAXBException, IOException {               
        LOGGER.info("The application is starting... {} ", pathToRoutFile);
        Route route = reader.readGpx(new File(pathToRoutFile));
        Driver driver = new Driver(route, new TimedDrivingStrategy());
        Thread driverThread = new Thread(driver);
        driverThread.start();
        driver.registerCarEventListener(evt -> {
        	 List<KafkaCarRecord> records = new ArrayList<>();
             records.add(new KafkaCarRecord("mycar", new KafkaCarPosition(evt.getLatitude().doubleValue(), evt.getLongitude().doubleValue())));
             KafkaCarEvent event = new KafkaCarEvent(records);
             kafkaService.publishCarEvent(event);
        });
    }
	
	

    void onStop(@Observes ShutdownEvent ev) {               
        LOGGER.info("The application is stopping...");
    }
}

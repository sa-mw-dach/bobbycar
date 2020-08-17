package com.redhat.bobbycar.carsim.clients;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.redhat.bobbycar.carsim.CarSimulatorApp;
import com.redhat.bobbycar.carsim.DefaultTestProfile;
import com.redhat.bobbycar.carsim.clients.model.Rss;
import com.redhat.bobbycar.carsim.consumer.ZoneChangeConsumer;

import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(DefaultTestProfile.class)
class OsmRoutesServiceIT {
	
	@Inject
	CarSimulatorApp app;

	@Inject
	ZoneChangeConsumer zoneChangeConsumer;
	
	@Inject
	@RestClient
	OsmRoutesService service;

    @BeforeAll
    public static void setup() {
    	CarSimulatorApp carSimulatorAppMock = Mockito.mock(CarSimulatorApp.class);
    	QuarkusMock.installMockForType(carSimulatorAppMock, CarSimulatorApp.class); 
    	ZoneChangeConsumer zoneChangeConsumer = Mockito.mock(ZoneChangeConsumer.class);
    	QuarkusMock.installMockForType(zoneChangeConsumer, ZoneChangeConsumer.class); 
    }
    
	@Test
	void testGetTraces() {
		Rss traces = service.getTraces();
		assertNotNull(traces);
		System.out.println(traces);
	}
	
	@Test
	void testGetData() {
		Object gpx = service.getRawTraceData("3373861");
		assertNotNull(gpx);
		System.out.println(gpx);
	}
}

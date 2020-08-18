package com.redhat.bobbycar.carsim.clients;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.redhat.bobbycar.carsim.CarSimulatorApp;
import com.redhat.bobbycar.carsim.DefaultTestProfile;
import com.redhat.bobbycar.carsim.clients.model.Zone;
import com.redhat.bobbycar.carsim.consumer.ZoneChangeConsumer;

import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(DefaultTestProfile.class)
class DataGridServiceIT {
	@Inject
	CarSimulatorApp app;

	@Inject
	ZoneChangeConsumer zoneChangeConsumer;
	
	@Inject
	@RestClient
	DataGridService service;

    @BeforeAll
    public static void setup() {
    	CarSimulatorApp carSimulatorAppMock = Mockito.mock(CarSimulatorApp.class);
    	QuarkusMock.installMockForType(carSimulatorAppMock, CarSimulatorApp.class); 
    	ZoneChangeConsumer zoneChangeConsumer = Mockito.mock(ZoneChangeConsumer.class);
    	QuarkusMock.installMockForType(zoneChangeConsumer, ZoneChangeConsumer.class); 
    }
    
	@Test
	void testGetTraces() {
		Zone zone = service.getZoneData("", MediaType.APPLICATION_JSON, "123");
		assertNotNull(zone);
		System.out.println(zone);
	}
}

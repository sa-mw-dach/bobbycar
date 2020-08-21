package com.redhat.bobbycar.carsim.cars.events;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.redhat.bobbycar.carsim.DatagridTestResource;
import com.redhat.bobbycar.carsim.DefaultTestProfile;
import com.redhat.bobbycar.carsim.KafkaTestResource;
import com.redhat.bobbycar.carsim.cars.EngineData;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(DefaultTestProfile.class)
@QuarkusTestResource(DatagridTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
class CarMetricsEventPublisherTest {
	private static final String ID = "de9bcc2e-b623-4b19-a2f9-a1bc8e81b45e";
	@Inject
	CarMetricsEventPublisher publisher;
	private static Monitor mock;
	
	public static class Monitor {
		public void check() {
			
		}
	}
	
	@BeforeAll
	public static void setUp() {
		mock = mock(Monitor.class);
	}
	
	@Test
	void testPublish() {
		CarMetricsEvent evt = new CarMetricsEvent();
		evt.setDriverId(ID);
		evt.setManufacturer("BMW");
		evt.setModel("M3 Coupe");
		evt.setEngineData(EngineData.builder().withCo2Emission(120).withFuelConsumptionPer100km(5.8).withGear(4)
				.withRpm(4000).withSpeedInKmh(87.8).build());
		publisher.publish(evt);
		verify(mock, atLeast(1)).check();
	}

	@Incoming("enginemetricscons")
	public void generate(byte[] payload) {
		mock.check();
	}
}

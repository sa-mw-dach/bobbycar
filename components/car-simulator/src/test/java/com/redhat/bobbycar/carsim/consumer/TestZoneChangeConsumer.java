package com.redhat.bobbycar.carsim.consumer;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.junit.jupiter.api.Test;

import com.redhat.bobbycar.carsim.DatagridTestResource;
import com.redhat.bobbycar.carsim.DefaultTestProfile;
import com.redhat.bobbycar.carsim.KafkaTestResource;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.smallrye.mutiny.Multi;

@QuarkusTest
@TestProfile(DefaultTestProfile.class)
@QuarkusTestResource(DatagridTestResource.class)
@QuarkusTestResource(KafkaTestResource.class)
class TestZoneChangeConsumer {

	private static final int EVENT_FREQUENCY_SECONDS = 1;
	private static final int EVENT_TIMEOUT = 3;
	private static final String ID = "de9bcc2e-b623-4b19-a2f9-a1bc8e81b45e";
	private Jsonb jsonb = JsonbBuilder.create();
	
	@Inject
	ZoneChangeConsumer consumer;

	@Test
	void test() throws InterruptedException {
		ZoneChangeListener mock = mock(ZoneChangeListener.class);
		consumer.registerZoneChangeListener(mock);
		SECONDS.sleep(EVENT_TIMEOUT);
		verify(mock, atLeast(1)).onZoneChange(createTestEvent());
	}

	@Outgoing("zonechangepub")
	public Multi<String> generate() {
		return Multi.createFrom().ticks().every(ofSeconds(EVENT_FREQUENCY_SECONDS))
	            .map(x -> jsonb.toJson(createTestEvent()));
	}

	private ZoneChangeEvent createTestEvent() {
		return new ZoneChangeEvent("A","B",ID);
	}
}

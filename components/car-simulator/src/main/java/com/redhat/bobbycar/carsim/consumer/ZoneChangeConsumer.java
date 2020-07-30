package com.redhat.bobbycar.carsim.consumer;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.clients.DataGridService;
import com.redhat.bobbycar.carsim.clients.model.Zone;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;

@ApplicationScoped
public class ZoneChangeConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneChangeConsumer.class);
	private Jsonb jsonb;
    @RestClient
    @Inject
    DataGridService dataGridService;
	
   
	public ZoneChangeConsumer() {
		LOGGER.info("Listening for zonechange events");
		jsonb = JsonbBuilder.create();
	}
	
	private Optional<Zone> retrieveZoneData(String zoneId) {
		return Optional.of(jsonb.fromJson(dataGridService.getZoneData("", zoneId), Zone.class));
	}
	
	@Incoming("zonechange")
    public void consume(byte[] raw) {
		LOGGER.info("ZoneChange");
		String event = new String(raw);
		ZoneChangeEvent zoneChangeEvent = jsonb.fromJson(event, ZoneChangeEvent.class);
		LOGGER.info("Event: {}", zoneChangeEvent);
		LOGGER.info("Zone: {}", retrieveZoneData(zoneChangeEvent.getNextZoneId()));
    }

	
}

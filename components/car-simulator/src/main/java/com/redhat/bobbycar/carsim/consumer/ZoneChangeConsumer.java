package com.redhat.bobbycar.carsim.consumer;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.bobbycar.carsim.clients.DataGridService;
import com.redhat.bobbycar.carsim.clients.model.Zone;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ZoneChangeConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneChangeConsumer.class);
	private ObjectMapper mapper = new ObjectMapper();

    @RestClient
    @Inject
    DataGridService dataGridService;
	
   
	public ZoneChangeConsumer() {
		LOGGER.info("Listening for zonechange events");
	}
	
	void onStart(@Observes StartupEvent ev) {               
		LOGGER.info("Zone: {}", retrieveZoneData("bobbycar-offenbach"));
		
    }
	
	private Optional<Zone> retrieveZoneData(String zoneId) {
		try {
			return Optional.of(mapper.readValue(dataGridService.getZoneData("", zoneId), Zone.class));
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while marshaling json data from zone data", e);
			return Optional.empty();
		}
	}
	
	@Incoming("zonechange")
    public void consume(byte[] raw) {
		LOGGER.info("ZoneChange");
		String event = new String(raw);
		try {
			ZoneChangeEvent zoneChangeEvent = mapper.readValue(event, ZoneChangeEvent.class);
			LOGGER.info("Event: {}", zoneChangeEvent);
			LOGGER.info("Zone: {}", retrieveZoneData(zoneChangeEvent.getNextZoneId()));
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while marshaling json data from event data", e);
		}
    }

	
}

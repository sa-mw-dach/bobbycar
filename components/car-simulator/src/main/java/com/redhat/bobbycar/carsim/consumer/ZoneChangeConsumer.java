package com.redhat.bobbycar.carsim.consumer;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;

@ApplicationScoped
public class ZoneChangeConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneChangeConsumer.class);
	private Jsonb jsonb;
	private Set<ZoneChangeListener> listeners = new HashSet<>();
   
	public ZoneChangeConsumer() {
		LOGGER.info("Listening for zonechange events");
		jsonb = JsonbBuilder.create();
	}
	
	@Incoming("zonechange")
    public void consume(byte[] raw) {
		LOGGER.info("ZoneChange");
		String event = new String(raw);
		ZoneChangeEvent zoneChangeEvent = jsonb.fromJson(event, ZoneChangeEvent.class);
		LOGGER.info("Event: {}", zoneChangeEvent);
		notifyZoneChangeListeners(zoneChangeEvent);
    }
	
	private void notifyZoneChangeListeners(ZoneChangeEvent event) {
		listeners.stream().forEach(l -> l.onZoneChange(event));
	}

	public void registerZoneChangeListener(ZoneChangeListener listener) {
		listeners.add(listener);
	}
	
}

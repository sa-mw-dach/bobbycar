package com.redhat.bobbycar.carsim.consumer;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ZoneChangeConsumer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneChangeConsumer.class);
	
	public ZoneChangeConsumer() {
		LOGGER.info("Listening for zonechange events");
	}
	
	@Incoming("zonechange")
    public void consume(byte[] raw) {
		LOGGER.info("ZoneChange");
		String event = new String(raw);
		LOGGER.info(event);
    }
}

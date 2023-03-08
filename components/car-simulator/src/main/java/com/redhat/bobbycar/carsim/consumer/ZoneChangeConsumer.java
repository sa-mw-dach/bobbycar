package com.redhat.bobbycar.carsim.consumer;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.InternalChannels;
import com.redhat.bobbycar.carsim.cloud.drogue.DeviceCommand;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangePayload;

import io.smallrye.common.annotation.Blocking;
import io.vertx.core.impl.ConcurrentHashSet;

@ApplicationScoped
public class ZoneChangeConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneChangeConsumer.class);
	private Set<ZoneChangeListener> listeners = new ConcurrentHashSet<>();

	public ZoneChangeConsumer() {
		LOGGER.info("Listening for zonechange events");
	}

	@Incoming(InternalChannels.ZONECHANGE)
	@Blocking
	public void consume(ZoneChangeEvent event) {
		LOGGER.info("ZoneChange");
		notifyZoneChangeListeners(event);
	}

	private void notifyZoneChangeListeners(ZoneChangeEvent event) {
		listeners.forEach(l -> l.onZoneChange(event));
	}

	public void registerZoneChangeListener(ZoneChangeListener listener) {
		listeners.add(listener);
	}

}

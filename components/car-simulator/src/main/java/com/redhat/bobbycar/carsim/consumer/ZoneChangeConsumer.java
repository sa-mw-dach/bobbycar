package com.redhat.bobbycar.carsim.consumer;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.cloud.Commands;
import com.redhat.bobbycar.carsim.cloud.drogue.DeviceCommand;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangePayload;

import io.vertx.core.impl.ConcurrentHashSet;

@ApplicationScoped
public class ZoneChangeConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneChangeConsumer.class);
	private Set<ZoneChangeListener> listeners = new ConcurrentHashSet<>();

	public ZoneChangeConsumer() {
		LOGGER.info("Listening for zonechange events");
	}

	@Incoming(Commands.CHANNEL_ZONECHANGE)
	public void consume(DeviceCommand<ZoneChangePayload> command) {
		LOGGER.info("ZoneChange");
		notifyZoneChangeListeners(new ZoneChangeEvent(command.getDevice(), command.getPayload()));
	}

	private void notifyZoneChangeListeners(ZoneChangeEvent event) {
		listeners.forEach(l -> l.onZoneChange(event));
	}

	public void registerZoneChangeListener(ZoneChangeListener listener) {
		listeners.add(listener);
	}

}

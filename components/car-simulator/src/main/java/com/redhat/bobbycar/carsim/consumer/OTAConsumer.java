package com.redhat.bobbycar.carsim.consumer;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.cloud.Commands;
import com.redhat.bobbycar.carsim.cloud.DeviceCommand;

import io.vertx.core.impl.ConcurrentHashSet;

@ApplicationScoped
public class OTAConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTAConsumer.class);

    private final Set<OTAListener> listeners = new ConcurrentHashSet<>();

    public OTAConsumer() {
        LOGGER.info("Creating OTAConsumer. Listening for over-the-air update events");
    }

    @Incoming(Commands.CHANNEL_OTA_UPDATE)
    public CompletionStage<Void> consume(Message<DeviceCommand<String>> message) {
        var device = message.getPayload().getDevice();
        var payload = message.getPayload().getPayload();
        LOGGER.debug("Consuming MQTT over-the-air update event");
        LOGGER.info("Received OTA - device: {}, payload: {}", device, payload);

        listeners.stream()
                .filter(l -> l.getVin().equals(device))
                .collect(Collectors.toList()).forEach(el -> {
                    LOGGER.info("Notifying OTA update listener for VIN: {}", el.getVin());
                    el.onUpdate(payload);
                });

        return message.ack();
    }

    public void registerOTAListener(String id, OTAListener listener) {
        listeners.add(listener);
    }

}

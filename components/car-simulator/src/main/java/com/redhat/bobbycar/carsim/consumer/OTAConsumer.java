package com.redhat.bobbycar.carsim.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import com.redhat.bobbycar.carsim.InternalChannels;
import com.redhat.bobbycar.carsim.cloud.drogue.DeviceCommand;

@ApplicationScoped
public class OTAConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTAConsumer.class);
    private final Set<OTAListener> listeners = new CopyOnWriteArraySet<>();

    public OTAConsumer() {
        LOGGER.info("Creating OTAConsumer. Listening for over-the-air update events");
    }

    @Incoming(InternalChannels.OTA_UPDATE)
    public CompletionStage<Void> consume(Message<DeviceCommand<String>> message) {
        LOGGER.debug("Consuming MQTT over-the-air update event");

        var device = message.getPayload().getDevice();
        var payload = message.getPayload().getPayload();
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

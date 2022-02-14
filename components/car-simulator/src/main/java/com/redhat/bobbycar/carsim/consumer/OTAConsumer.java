package com.redhat.bobbycar.carsim.consumer;

import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@ApplicationScoped
public class OTAConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTAConsumer.class);
    private Map<String, OTAListener> listeners = new HashMap<>();

    public OTAConsumer() {
        LOGGER.info("Creating OTAConsumer. Listening for over-the-air update events");
    }

    @Incoming("ota-updates")
    public CompletionStage<Void> consume(MqttMessage<byte[]> message) {
        LOGGER.debug("Consuming MQTT over-the-air update event");
        String payload = new String(message.getPayload());
        LOGGER.info("Received OTA paylod: " + payload + "\n from topic " + message.getTopic());

        listeners.values().stream()
            .filter(l -> l.getVin().equals(message.getTopic().substring(4)))
            .collect(Collectors.toList()).forEach(el -> {
                LOGGER.info("Notifying OTA update listener for VIN: "+el.getVin());
                el.onUpdate(payload);
            });

        return message.ack();
    }

    private void notifyOTAListeners(String key, String event) {
        LOGGER.info("Notifying OTA update listener: "+key);
        listeners.get(key).onUpdate(event);
    }

    public void registerOTAListener(String id, OTAListener listener) {
        listeners.put(id, listener);
    }

}

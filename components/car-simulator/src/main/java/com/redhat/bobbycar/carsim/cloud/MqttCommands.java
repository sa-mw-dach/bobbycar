package com.redhat.bobbycar.carsim.cloud;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.consumer.model.ZoneChangePayload;

import io.quarkus.runtime.Startup;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;

@Startup
@ApplicationScoped
public class MqttCommands implements Commands {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttCommands.class);

    private final Jsonb json = JsonbBuilder.create();

    @Inject
    @Channel(CHANNEL_ZONECHANGE)
    Emitter<DeviceCommand<ZoneChangePayload>> zonechange;

    @Inject
    @Channel(CHANNEL_OTA_UPDATE)
    Emitter<DeviceCommand<String>> ota;

    @Incoming("drogue-commands")
    CompletionStage<Void> deviceCommands(Message<byte[]> message) {

        if (message instanceof MqttMessage) {
            return handleCommand((MqttMessage<byte[]>) message)
                    .thenCompose(x -> message.ack());
        } else {
            return message.ack();
        }

    }

    private CompletionStage<Void> handleCommand(MqttMessage<byte[]> message) {
        var topic = message.getTopic();
        LOGGER.info("Device message - topic: {}", topic);

        return CommandMetadata.fromCommandTopic(topic)
                .map(command -> handleDeviceCommand(command, message.getPayload()))
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    private CompletionStage<Void> handleDeviceCommand(CommandMetadata meta, byte[] payload) {
        if (LOGGER.isInfoEnabled()) {
            var p = payload == null ? null : new String(payload);
            LOGGER.info("Received command - {}, payload: {}", meta, p);
        }
        if (payload == null) {
            return CompletableFuture.completedFuture(null);
        }

        switch (meta.getCommand()) {
        case "zonechange":
            var commandPayload = json.fromJson(new ByteArrayInputStream(payload), ZoneChangePayload.class);
            return zonechange.send(new DeviceCommand<>(meta.getDevice(), commandPayload));
        case "ota":
            return ota.send(new DeviceCommand<>(meta.getDevice(), new String(payload)));
        default:
            return CompletableFuture.completedFuture(null);
        }

    }

}

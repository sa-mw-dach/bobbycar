package com.redhat.bobbycar.carsim.cloud;

import java.io.ByteArrayInputStream;
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
    CompletionStage<Void> deviceCommands(final MqttMessage<byte[]> message) {
        var topic = message.getTopic();
        LOGGER.info("Device message - topic: {}", topic);

        return CommandMetadata.fromCommandTopic(topic)
                .map(command -> handleDeviceCommand(message, command, message.getPayload()))
                .orElseGet(message::ack);
    }

    private CompletionStage<Void> handleDeviceCommand(
            final Message<?> message,
            final CommandMetadata meta,
            final byte[] payload)
    {

        if (LOGGER.isInfoEnabled()) {
            var p = payload == null ? null : new String(payload);
            LOGGER.info("Received command - {}, payload: {}", meta, p);
        }

        if (payload == null) {
            // all our handlers require some form of payload
            return message.ack();
        }

        switch (meta.getCommand()) {
        case "zonechange":
            var commandPayload = this.json.fromJson(
                    new ByteArrayInputStream(payload), ZoneChangePayload.class
            );
            return this.zonechange.send(new DeviceCommand<>(meta.getDevice(), commandPayload));
        case "ota":
            return this.ota.send(new DeviceCommand<>(meta.getDevice(), new String(payload)));
        default:
            LOGGER.info("Unknown message received - command: {}", meta);
            return message.ack();
        }

    }

}

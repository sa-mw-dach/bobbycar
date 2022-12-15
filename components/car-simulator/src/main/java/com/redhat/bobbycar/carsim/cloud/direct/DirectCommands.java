package com.redhat.bobbycar.carsim.cloud.direct;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.InternalChannels;
import com.redhat.bobbycar.carsim.Profiles;
import com.redhat.bobbycar.carsim.cloud.drogue.DeviceCommand;
import com.redhat.bobbycar.carsim.cloud.drogue.MqttCommands;
import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.Startup;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;

@UnlessBuildProfile(Profiles.DROGUE)
@Startup
@ApplicationScoped
public class DirectCommands {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttCommands.class);

    @Inject
    Jsonb jsonb;

    @Incoming("zonechange")
    @Outgoing(InternalChannels.ZONECHANGE)
    ZoneChangeEvent consumeZoneChange(byte[] raw) {
        LOGGER.info("ZoneChange");

        ZoneChangeEvent zoneChangeEvent = jsonb.fromJson(new ByteArrayInputStream(raw), ZoneChangeEvent.class);
        LOGGER.info("Event: {}", zoneChangeEvent);

        return zoneChangeEvent;
    }

    @Incoming("ota-update")
    @Outgoing(InternalChannels.OTA_UPDATE)
    public DeviceCommand<String> consume(MqttMessage<String> message) {
        LOGGER.debug("Consuming MQTT over-the-air update event");

        LOGGER.info("Received OTA payload: {}\n from topic {}", message.getPayload(), message.getTopic());
        var vin = message.getTopic().substring(4);

        return new DeviceCommand<>(vin, message.getPayload());
    }

}

package com.redhat.bobbycar.carsim.cloud;

import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.cars.events.CarMetricsEvent;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarPosition;
import com.redhat.bobbycar.carsim.drivers.Driver;

import io.smallrye.reactive.messaging.mqtt.MqttMessage;

@ApplicationScoped
public class MqttTelemetry implements Telemetry {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttTelemetry.class);

    private static final Jsonb JSONB = JsonbBuilder.create();

    @ConfigProperty(name = "drogue.application")
    String application;

    @PostConstruct
    void start() {
        LOGGER.info("Drogue IoT Telemetry Client (MQTT)");
        LOGGER.info("   Application: {}", this.application);
    }

    @Inject
    @Channel("drogue-telemetry")
    Emitter<byte[]> telemetry;

    @Override
    public void publishCarEvent(final Driver driver, final KafkaCarPosition carEvent) {
        var payload = JSONB.toJson(carEvent).getBytes(StandardCharsets.UTF_8);
        var device = driver.getId();
        this.telemetry.send(MqttMessage.of(String.format("car/%s", device), payload));
    }

    @Override
    public void publishCarMetricEvent(CarMetricsEvent evt) {
        var payload = JSONB.toJson(evt).getBytes(StandardCharsets.UTF_8);
        var device =  evt.getDriverId();
        this.telemetry.send(MqttMessage.of(String.format("carMetrics/%s", device), payload));
    }
}

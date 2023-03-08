package com.redhat.bobbycar.carsim.cloud.direct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.net.ssl.SSLException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.Profiles;
import com.redhat.bobbycar.carsim.cars.events.CarMetricsEvent;
import com.redhat.bobbycar.carsim.clients.KafkaService;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarEvent;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarPosition;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarRecord;
import com.redhat.bobbycar.carsim.cloud.Telemetry;
import com.redhat.bobbycar.carsim.drivers.Driver;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.smallrye.reactive.messaging.annotations.Broadcast;

@UnlessBuildProfile(Profiles.DROGUE)
@ApplicationScoped
public class DirectTelemetry implements Telemetry  {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectTelemetry.class);

    // Rest Clients
    @Inject
    @RestClient
    KafkaService kafkaService;

    @ConfigProperty(name = "com.redhat.bobbycar.carsim.kafka.apiKey")
    Optional<String> apiKey;

    @Inject @Channel("enginemetrics") @Broadcast
    Emitter<CarMetricsEvent> emitter;

    @Override
    public void publishCarEvent(Driver driver, KafkaCarPosition evt) {
        List<KafkaCarRecord> records = new ArrayList<>();
        records.add(new KafkaCarRecord(driver.getId(), evt));
        KafkaCarEvent event = new KafkaCarEvent(records);
        try {
            kafkaService.publishCarEvent(apiKey.orElse(null), event);
        } catch(Exception e) {
            LOGGER.error("Error publishing car event to kafka", e);
            if (e.getCause() instanceof SSLException) {
                LOGGER.error("Cannot recover from SSL error. Shutting down.");
                throw e;
                //TODO Shutdown Service or set health check to unhealthy state
            }
        }
    }

    @Override
    public void publishCarMetricEvent(CarMetricsEvent evt) {
        emitter.send(evt);
    }

    @Override
    public String toString() {
        return "Direct telemetry";
    }
}

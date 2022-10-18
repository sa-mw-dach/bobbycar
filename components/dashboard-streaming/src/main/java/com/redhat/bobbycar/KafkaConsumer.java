package com.redhat.bobbycar;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;

@ApplicationScoped
public class KafkaConsumer {

    @Inject
    CarEventSocket carEventSocket;

    @Inject
    CarMetricsSocket carMetricsSocket;

    @Inject
    ZoneChangeSocket zoneChangeSocket;

    @Inject
    CarMetricsAggregatedSocket carMetricsAggregatedSocket;

    @Inject
    SpeedAlertSocket speedAlertSocket;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class.getName());

    @Incoming("drogue-events")
    public void consumeEvent(CloudEvent event) {

        LOGGER.debug("Received Drogue event: {}", event);

        if (event.getSubject() == null) {
            return;
        }

        var subject = event.getSubject();

        if ("car".equals(subject)) {
            stringData(event)
                    .ifPresent(this.carEventSocket::broadcast);
        } else if ("carMetrics".equals(subject)) {
            stringData(event)
                    .ifPresent(this.carMetricsSocket::broadcast);
        }

    }

    /**
     * Extract the Cloud Events data as {@code String}.
     *
     * @param event The cloud event.
     * @return The data as string, may be {@link Optional#empty()}, but never {@code null}.
     */
    private static Optional<String> stringData(final CloudEvent event) {
        return Optional.ofNullable(event)
                .map(CloudEvent::getData)
                .map(CloudEventData::toBytes)
                .map(String::new);
    }

    @Incoming("bobbycar-zonechange")
    public void consumeZoneChange(String zoneChangeEvent) {
        LOGGER.info("Received Kafka zone change event:" + zoneChangeEvent);
        zoneChangeSocket.broadcast(zoneChangeEvent);
    }

    @Incoming("bobbycar-metrics-aggregated")
    public void consumeAggregatedMetrics(String metric) {
        LOGGER.info("Received Kafka aggregated metric event:" + metric);
        carMetricsAggregatedSocket.broadcast(metric);
    }

    @Incoming("bobbycar-speed-alert")
    public void consumeSpeedAlerts(String alert) {
        LOGGER.info("Received Kafka speed alert event:" + alert);
        speedAlertSocket.broadcast(alert);
    }

}



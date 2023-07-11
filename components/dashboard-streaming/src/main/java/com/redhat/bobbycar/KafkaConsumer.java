package com.redhat.bobbycar;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class KafkaConsumer {

    @Inject
    ZoneChangeSocket zoneChangeSocket;
    @Inject
    CarMetricsAggregatedSocket carMetricsAggregatedSocket;
    @Inject
    SpeedAlertSocket speedAlertSocket;

    private static final Logger LOGGER = Logger.getLogger(KafkaConsumer.class.getName());

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



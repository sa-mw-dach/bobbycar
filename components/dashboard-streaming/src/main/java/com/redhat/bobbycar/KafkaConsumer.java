package com.redhat.bobbycar;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


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

    private static final Logger LOGGER = Logger.getLogger(KafkaConsumer.class.getName());

    @Incoming("bobbycar-gps")
    public void consumeGps(String carEvent) {
        LOGGER.info("Received Kafka gps data:" + carEvent);
        carEventSocket.broadcast(carEvent);
    }

    @Incoming("bobbycar-metrics")
    public void consumeMetrics(String metric) {
        LOGGER.info("Received Kafka engine data:" + metric);
        carMetricsSocket.broadcast(metric);
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
    public void consumeSpeedALerts(String alert) {
        LOGGER.info("Received Kafka speed alert event:" + alert);
        carMetricsAggregatedSocket.broadcast(alert);
    }

}



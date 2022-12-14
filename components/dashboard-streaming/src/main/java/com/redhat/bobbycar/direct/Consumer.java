package com.redhat.bobbycar.direct;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.redhat.bobbycar.CarEventSocket;
import com.redhat.bobbycar.CarMetricsSocket;

import io.quarkus.arc.profile.UnlessBuildProfile;

@UnlessBuildProfile("drogue")
@ApplicationScoped
public class Consumer {

    private static final Logger LOGGER = Logger.getLogger(Consumer.class.getName());

    @Inject
    CarEventSocket carEventSocket;
    @Inject
    CarMetricsSocket carMetricsSocket;

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

}

package com.redhat.bobbycar.drogue;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.CarEventSocket;
import com.redhat.bobbycar.CarMetricsSocket;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.quarkus.arc.profile.IfBuildProfile;

@IfBuildProfile("drogue")
@ApplicationScoped
public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    @Inject
    CarEventSocket carEventSocket;
    @Inject
    CarMetricsSocket carMetricsSocket;

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

}

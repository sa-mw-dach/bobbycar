package com.redhat.bobbycar.carsim.cars.events;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.cloud.Telemetry;

@ApplicationScoped
public class CarMetricsEventPublisher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CarMetricsEventPublisher.class);
	
//	@Inject @Channel("enginemetrics")
//	Publisher<CarMetricsEvent> publisherOfPayloads;

	@Inject
    Telemetry telemetry;

	public void publish(CarMetricsEvent evt) {
		LOGGER.debug("Publishing CarMetricsEvent: {}", evt);
		telemetry.publishCarMetricEvent(evt);
	}
	
	/*@Outgoing("enginemetrics")
	public Publisher<CarMetricsEvent> generate() {
		return publisherOfPayloads;
	}*/
}

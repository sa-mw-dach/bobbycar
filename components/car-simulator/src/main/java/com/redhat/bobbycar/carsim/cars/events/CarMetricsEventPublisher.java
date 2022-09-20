package com.redhat.bobbycar.carsim.cars.events;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class CarMetricsEventPublisher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CarMetricsEventPublisher.class);
	
	@Inject @Channel("enginemetrics") @Broadcast
	Emitter<CarMetricsEvent> emitter;
//	@Inject @Channel("enginemetrics")
//	Publisher<CarMetricsEvent> publisherOfPayloads;
	
	public void publish(CarMetricsEvent evt) {
		LOGGER.debug("Publishing CarMetricsEvent: {}", evt);
	    emitter.send(evt);
	}
	
	/*@Outgoing("enginemetrics")
	public Publisher<CarMetricsEvent> generate() {
		return publisherOfPayloads;
	}*/
}

package com.redhat.bobbycar.carsim.cars.events;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.cloud.Telemetry;

@ApplicationScoped
public class CarMetricsEventPublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(CarMetricsEventPublisher.class);

	@Inject
	Telemetry telemetry;

	public void publish(CarMetricsEvent evt) {
		LOGGER.debug("Publishing CarMetricsEvent: {}", evt);
		telemetry.publishCarMetricEvent(evt);
	}
}

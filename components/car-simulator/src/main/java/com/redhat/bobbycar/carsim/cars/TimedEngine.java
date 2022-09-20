package com.redhat.bobbycar.carsim.cars;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.cars.events.EngineMetricsEvent;
import com.redhat.bobbycar.carsim.cars.events.EngineMetricsEventListener;
import com.redhat.bobbycar.carsim.drivers.RouteNotSupportedException;
import com.redhat.bobbycar.carsim.routes.Route;
import com.redhat.bobbycar.carsim.routes.RoutePoint;

public class TimedEngine implements Engine{
	private static final Logger LOGGER = LoggerFactory.getLogger(TimedEngine.class);
	private static final String ROUTE_NOT_SUPPORTED_MSG = "Route points must contain time";
	private static final MathContext MATH_CTX = new MathContext(12);
	
	private final int speedVariationInKmH;
	private RoutePoint previous;
	private RoutePoint next;
	private EngineConfiguration config;
	private static final BigDecimal THOUSAND = new BigDecimal(1000);
	private static final BigDecimal SECONDS_PER_HOUR = new BigDecimal(60 * 60);
	private static final int DATA_RESOULUTION_MS = 5000;
	private final Random random = new Random();
	private final Optional<EngineMetrics> metrics;
	private boolean stopped = false;
	private List<EngineMetricsEventListener> eventListeners = new ArrayList<>();


	private TimedEngine(Builder builder) {
		this.speedVariationInKmH = builder.speedVariationInKmH;
		this.previous = builder.startingPoint;
		this.next = builder.startingPoint;
		this.config = builder.config;
		this.metrics = builder.metrics;
		if(builder.startingPoint == null || !builder.startingPoint.getTime().isPresent()) {
			List<RoutePoint> points = new ArrayList<>();
			if (builder.startingPoint != null) {
				points.add(builder.startingPoint);
			}
			throw new RouteNotSupportedException(ROUTE_NOT_SUPPORTED_MSG, new Route("", points));
		}
		metrics.ifPresent(m -> registerEventListener(evt -> m.updateEngineData(evt.getEngineData())));
	}

	public EngineConfiguration getEngineConfiguration() {
		return this.config;
	}
	
	@Override
	public TimedEngine nextRoutePoint(RoutePoint next) {
		if(!next.getTime().isPresent()) {
			throw new RouteNotSupportedException(ROUTE_NOT_SUPPORTED_MSG, new Route("", List.of(this.previous, next)));
		}
		this.previous = this.next;
		this.next = next;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Previous is now {} and next is {}", previous, next);
		}
		return this;
	}
	
	private Optional<Double> currentSpeedInKmH() {
		BigDecimal distanceInKilometers = BigDecimal.valueOf(previous.distanceInMetersTo(next)).divide(THOUSAND);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Distance is {}km", distanceInKilometers);
		}
		Optional<Duration> duration = previous.durationBetween(next); 
		return duration.flatMap(d -> {
			if(!d.isZero()) {
				return Optional.of(calculateSpeed(distanceInKilometers, d));
			} else {
				return Optional.empty();
			}
		});
	}

	private Double calculateSpeed(BigDecimal distanceInKilometers, Duration d) {
		BigDecimal durationInHours = new BigDecimal(d.get(ChronoUnit.SECONDS)).divide(SECONDS_PER_HOUR, MATH_CTX);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Duration between {} and {} is {} hours", previous, next, durationInHours);
		}
		int randomSpeedVariationInKmH = speedVariationInKmH > 0 ? random.nextInt(speedVariationInKmH) : 0;
		BigDecimal speedVariationAmount = new BigDecimal(randomSpeedVariationInKmH * random.nextInt() % 2 == 0 ? 1 : -1);
		return distanceInKilometers.divide(durationInHours, MATH_CTX).add(speedVariationAmount).doubleValue();
	}

	private double calculateCo2InRange(int min, int max){
		double co2 = ((Math.random() * (max - min)) + min);
		return BigDecimal.valueOf(co2).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@Override
	public EngineData currentData() {
		double co2 = calculateCo2InRange(100,120);
		Optional<Double> currentSpeedInKmH = currentSpeedInKmH();
		LOGGER.debug("Current speed {}", currentSpeedInKmH);
		com.redhat.bobbycar.carsim.cars.EngineData.Builder builder = EngineData.builder();
		currentSpeedInKmH.ifPresent(speed -> {
			//config.co2FromSpeed(speed).ifPresent(builder::withCo2Emission);
			builder.withCo2Emission(co2);
			config.fuelConsumptionPer100KmFromSpeed(speed).ifPresent(builder::withFuelConsumptionPer100km);
			config.gearFromSpeed(speed).ifPresent(builder::withGear);
			config.rpmFromSpeed(speed).ifPresent(builder::withRpm);
			builder.withSpeedInKmh(speed);
		});
		return builder.build();
	}

	@Override
	public void run() {
		while(!stopped) {
			EngineData currentData = currentData();
			eventListeners.stream().forEach(l -> l.update(new EngineMetricsEvent(currentData)));
			try {
				TimeUnit.MILLISECONDS.sleep(DATA_RESOULUTION_MS);
			} catch (InterruptedException e) {
				LOGGER.warn("Error pausing engine thread", e);
	    		Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void stop() {
		stopped = true;
	}
	
	public void registerEventListener(EngineMetricsEventListener eventListener) {
		this.eventListeners.add(eventListener);
	}

	/**
	 * Creates builder to build {@link TimedEngine}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link TimedEngine}.
	 */
	public static final class Builder {
		private int speedVariationInKmH;
		private RoutePoint startingPoint;
		private EngineConfiguration config;
		private Optional<EngineMetrics> metrics = Optional.empty();

		private Builder() {
		}

		public Builder withSpeedVariationInKmH(int speedVariationInKmH) {
			this.speedVariationInKmH = speedVariationInKmH;
			return this;
		}

		public Builder withStartingPoint(RoutePoint startingPoint) {
			this.startingPoint = startingPoint;
			return this;
		}


		public Builder withConfig(EngineConfiguration config) {
			this.config = config;
			return this;
		}

		public Builder withMetrics(EngineMetrics metrics) {
			this.metrics = Optional.ofNullable(metrics);
			return this;
		}

		public TimedEngine build() {
			return new TimedEngine(this);
		}
	}
	
	
}

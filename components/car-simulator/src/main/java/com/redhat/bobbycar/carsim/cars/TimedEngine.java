package com.redhat.bobbycar.carsim.cars;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final BigDecimal THOUSAND = new BigDecimal(1000);
	private static final BigDecimal SECONDS_PER_HOUR = new BigDecimal(60 * 60);
	private Random random = new Random();
	
	
	public TimedEngine(int speedVariationInKmH, RoutePoint startingPoint) {
		super();
		this.speedVariationInKmH = speedVariationInKmH;
		this.previous = startingPoint;
		this.next = startingPoint;
		if(!startingPoint.getTime().isPresent()) {
			throw new RouteNotSupportedException(ROUTE_NOT_SUPPORTED_MSG, new Route("", List.of(startingPoint)));
		}
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
	
	@Override
	public double currentSpeedInKmH() {
		BigDecimal distanceInKilometers = BigDecimal.valueOf(previous.distanceInMetersTo(next)).divide(THOUSAND);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Distance is {}km", distanceInKilometers);
		}
		Optional<Duration> duration = previous.durationBetween(next); 
		return duration.map(d -> {
			if(!d.isZero()) {
				return calculateSpeed(distanceInKilometers, d);
			} else {
				return 0.0;
			}
		}).orElse(0.0);
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
	
}

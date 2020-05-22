package com.redhat.bobbycar.carsim.drivers;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.CarEvent;
import com.redhat.bobbycar.carsim.routes.Route;
import com.redhat.bobbycar.carsim.routes.RoutePoint;

public class TimedDrivingStrategy implements DrivingStrategy{
	private static final Logger LOGGER = LoggerFactory.getLogger(TimedDrivingStrategy.class);
	private final double factor;
	private Optional<Long> lastActionTimeMillis = Optional.empty();
	private final Optional<TimedDrivingStrategyMetrics> metrics;
	
	private TimedDrivingStrategy(Builder builder) {
		this.factor = builder.factor;
		this.lastActionTimeMillis = builder.lastActionTimeMillis;
		this.metrics = Optional.ofNullable(builder.metrics);
	}

	@Override
	public boolean supports(Route route) {
		return route.getPoints().allMatch(p -> !p.getTime().isEmpty());
	}
	
	@Override
	public void drive(Optional<RoutePoint> from, RoutePoint to, Consumer<CarEvent> consumer) {
		LOGGER.debug("Calculating duration between {} and {}", from, to);
		lastActionTimeMillis.ifPresent(l -> LOGGER.debug("Time passed after last action: {}ms", System.currentTimeMillis() - l));
		Optional<Long> duration = from.flatMap(f -> desiredIntervalToLastPoint(f, to).flatMap(d -> 
			lastActionTimeMillis.map(l -> d.toMillis() - (System.currentTimeMillis() - l))
		));
		duration.ifPresent(remainingWaitTime -> {
			try {
				LOGGER.debug("Waiting {}ms to arrive at next route point", remainingWaitTime);
				long sleepTime = Math.round(remainingWaitTime / factor);
				metrics.ifPresent(m -> m.setSkewMillis(-sleepTime));
				if (sleepTime > 0) {
					TimeUnit.MILLISECONDS.sleep(sleepTime);
				}
				consumer.accept(new CarEvent(to.getLongitude(), to.getLatitude(), to.getElevation(), Optional.of(ZonedDateTime.now())));
				
			}catch (InterruptedException e) {
				LOGGER.error("Cannot pause thread to wait for next point", e);
				Thread.currentThread().interrupt();
			}
		});
		if (from.isEmpty()) {
			LOGGER.debug("First event, driving instantly");
			consumer.accept(new CarEvent(to.getLongitude(), to.getLatitude(), to.getElevation(), Optional.of(ZonedDateTime.now())));
		}
		lastActionTimeMillis = Optional.of(System.currentTimeMillis());
	}

	private Optional<Duration> desiredIntervalToLastPoint(RoutePoint from, RoutePoint to) {
		return from.getTime()
					.flatMap(lt -> to.getTime()
						.map(tp -> Duration.between(lt, tp)));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private double factor = 1;
		private Optional<Long> lastActionTimeMillis = Optional.empty();
		private TimedDrivingStrategyMetrics metrics;

		private Builder() {
		}

		public Builder withFactor(double factor) {
			this.factor = factor;
			return this;
		}
		
		public Builder withMetrics(TimedDrivingStrategyMetrics metrics) {
			this.metrics = metrics;
			return this;
		}

		public Builder withLastActionTimeMillis(Optional<Long> lastActionTimeMillis) {
			this.lastActionTimeMillis = lastActionTimeMillis;
			return this;
		}

		public TimedDrivingStrategy build() {
			return new TimedDrivingStrategy(this);
		}
	}


	
}

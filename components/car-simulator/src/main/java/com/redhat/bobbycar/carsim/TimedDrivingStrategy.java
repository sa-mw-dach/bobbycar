package com.redhat.bobbycar.carsim;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedDrivingStrategy implements DrivingStrategy{
	private static final Logger LOGGER = LoggerFactory.getLogger(DrivingStrategy.class);
	private final double factor;
	private Optional<Long> lastActionTimeMillis = Optional.empty();
	
	private TimedDrivingStrategy(Builder builder) {
		this.factor = builder.factor;
		this.lastActionTimeMillis = builder.lastActionTimeMillis;
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
				if (sleepTime > 0) {
					TimeUnit.MILLISECONDS.sleep(sleepTime);
				}
				
				consumer.accept(new CarEvent(to.getLongitude(), to.getLatitude(), to.getElevation(), Optional.of(ZonedDateTime.now())));
			}catch (InterruptedException e) {
				//TODO Error handling
				LOGGER.error("", e);
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

		private Builder() {
		}

		public Builder withFactor(double factor) {
			this.factor = factor;
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

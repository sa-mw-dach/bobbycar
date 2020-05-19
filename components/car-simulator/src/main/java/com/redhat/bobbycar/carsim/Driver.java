package com.redhat.bobbycar.carsim;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Driver implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
	private final List<CarEventListener> listeners = new ArrayList<>();
	private Route route;
	
	private Optional<RoutePoint> lastPoint = Optional.empty();
	private final DrivingStrategy drivingStrategy;
	private final DriverMetrics metrics = new DriverMetrics();
	private final UUID id = UUID.randomUUID();
	private final String routeName;
	private final boolean repeat;

	private Driver(Builder builder) {
		this.route = builder.route;
		this.drivingStrategy = builder.drivingStrategy;
		this.routeName = builder.route.getName();
		this.repeat = builder.repeat;
		if(!drivingStrategy.supports(builder.route)) {
			throw new RouteNotSupportedException("Route not supported for driving strategy", builder.route);
		}
	}

	
	public void registerCarEventListener(CarEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		metrics.setStart(ZonedDateTime.now());
		LOGGER.debug("I am driving");
		do {
			route.getPoints().forEach(to -> {
				LOGGER.debug("to {}", to);
				drivingStrategy.drive(lastPoint, to, this::notifyListeners);
				lastPoint = Optional.of(to);
			});
			lastPoint = Optional.empty();
		} while (repeat);
		metrics.setEnd(ZonedDateTime.now());
	}
	
	private void notifyListeners(CarEvent event) {
		for (CarEventListener carEventListener : listeners) {
			carEventListener.update(event);
		}
	}

	public DriverMetrics getMetrics() {
		return metrics;
	}

	public UUID getId() {
		return id;
	}

	public String getRouteName() {
		return routeName;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Route route;
		private DrivingStrategy drivingStrategy;
		private boolean repeat;

		private Builder() {
		}

		public Builder withRoute(Route route) {
			this.route = route;
			return this;
		}

		public Builder withDrivingStrategy(DrivingStrategy drivingStrategy) {
			this.drivingStrategy = drivingStrategy;
			return this;
		}

		public Builder withRepeat(boolean repeat) {
			this.repeat = repeat;
			return this;
		}

		public Driver build() {
			return new Driver(this);
		}
	}

}

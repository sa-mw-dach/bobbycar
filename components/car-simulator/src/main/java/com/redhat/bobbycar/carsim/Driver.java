package com.redhat.bobbycar.carsim;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Driver implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
	private final List<CarEventListener> listeners = new ArrayList<>();
	private Stream<RoutePoint> routeStream;
	private Optional<RoutePoint> lastPoint = Optional.empty();
	private final DrivingStrategy drivingStrategy;
	private final DriverMetrics metrics = new DriverMetrics();
	private final UUID id = UUID.randomUUID();
	private final String routeName;
	
	public Driver(Route route, DrivingStrategy drivingStrategy) {
		super();
		this.routeStream = route.getPoints();
		this.drivingStrategy = drivingStrategy;
		this.routeName = route.getName();
		if(!drivingStrategy.supports(route)) {
			throw new RouteNotSupportedException("Route not supported for driving strategy", route);
		}
	}
	
	public void registerCarEventListener(CarEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		metrics.setStart(ZonedDateTime.now());
		LOGGER.debug("I am driving");
		routeStream.forEach(to -> {
			LOGGER.debug("to {}", to);
			drivingStrategy.drive(lastPoint, to, this::notifyListeners);
			lastPoint = Optional.of(to);
		});
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

}

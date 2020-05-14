package com.redhat.bobbycar.carsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Driver implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
	private final List<CarEventListener> listeners = new ArrayList<>();
	private Stream<RoutePoint> routeStream;
	private Optional<RoutePoint> lastPoint = Optional.empty();
	private final DrivingStrategy drivingStrategy;
	
	public Driver(Route route, DrivingStrategy drivingStrategy) {
		super();
		this.routeStream = route.getPoints();
		this.drivingStrategy = drivingStrategy;
		if(!drivingStrategy.supports(route)) {
			throw new RouteNotSupportedException("Route not supported for driving strategy", route);
		}
	}
	
	public void registerCarEventListener(CarEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		LOGGER.debug("I am driving");
		routeStream.forEach(to -> {
			LOGGER.debug("to {}", to);
			drivingStrategy.drive(lastPoint, to, this::notifyListeners);
			lastPoint = Optional.of(to);
		});
	}
	
	private void notifyListeners(CarEvent event) {
		for (CarEventListener carEventListener : listeners) {
			carEventListener.update(event);
		}
	}
}

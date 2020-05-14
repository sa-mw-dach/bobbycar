package com.redhat.bobbycar.carsim;

import java.util.Optional;
import java.util.function.Consumer;

public interface DrivingStrategy {
	
	public boolean supports(Route route);

	void drive(Optional<RoutePoint> from, RoutePoint to, Consumer<CarEvent> consumer);

}

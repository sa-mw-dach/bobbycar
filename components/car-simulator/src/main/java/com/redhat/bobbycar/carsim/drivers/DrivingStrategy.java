package com.redhat.bobbycar.carsim.drivers;

import java.util.Optional;
import java.util.function.Consumer;

import com.redhat.bobbycar.carsim.CarEvent;
import com.redhat.bobbycar.carsim.routes.Route;
import com.redhat.bobbycar.carsim.routes.RoutePoint;

public interface DrivingStrategy {
	
	public boolean supports(Route route);

	void drive(Optional<RoutePoint> from, RoutePoint to, Consumer<CarEvent> consumer);

}

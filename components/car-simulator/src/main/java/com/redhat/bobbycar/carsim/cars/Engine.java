package com.redhat.bobbycar.carsim.cars;

import com.redhat.bobbycar.carsim.routes.RoutePoint;

public interface Engine {

	double currentSpeedInKmH();

	TimedEngine nextRoutePoint(RoutePoint next);

}

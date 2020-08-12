package com.redhat.bobbycar.carsim.cars;

import com.redhat.bobbycar.carsim.routes.RoutePoint;

public interface Engine extends Runnable{

	EngineData currentData();

	TimedEngine nextRoutePoint(RoutePoint next);
	
	void stop();
}

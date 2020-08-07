package com.redhat.bobbycar.carsim.cars;

import com.redhat.bobbycar.carsim.routes.RoutePoint;

public class Car {
	private final String model;
	private final String manufacturer;
	private final Engine engine;
	
	public Car(String model, String manufacturer, RoutePoint startingPoint) {
		super();
		this.model = model;
		this.manufacturer = manufacturer;
		this.engine = new TimedEngine(5, startingPoint);
	}
	
	public Car driveTo(RoutePoint target) {
		engine.nextRoutePoint(target);
		return this;
	}

	public String getModel() {
		return model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public double getCurrentSpeedInKmH() {
		return engine.currentSpeedInKmH();
	}
	
	public double getCurrentRpm() {
		return -1;
	}
	
	public double getCurrentFuelConsumption() {
		return -1;
	}
	
	public double getCo2Emissions() {
		return -1;
	}
	
}

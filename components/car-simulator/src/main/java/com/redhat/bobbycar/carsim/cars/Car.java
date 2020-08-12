package com.redhat.bobbycar.carsim.cars;

import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.routes.RoutePoint;

public class Car {
	private final String model;
	private final String manufacturer;
	private final Engine engine;
	private final UUID driverId;
	private static final Logger LOGGER = LoggerFactory.getLogger(Car.class);
	
	public Car(String model, String manufacturer, RoutePoint startingPoint, UUID driverId) {
		this(model, manufacturer, startingPoint, driverId, null);
	}
	
	public Car(String model, String manufacturer, RoutePoint startingPoint, UUID driverId, EngineMetrics metrics) {
		super();
		this.model = model;
		this.manufacturer = manufacturer;
		this.driverId = driverId;
		try {
			this.engine = new TimedEngine(5, startingPoint, new JsonEngineConfiguration(), metrics);
		} catch (FileNotFoundException e) {
			throw new EngineException("Engine configuration could not be loaded", e);
		}
	}

	public void start(Executor executor) {
		LOGGER.debug("Starting engine");
		CompletableFuture.runAsync(this.engine, executor);
	}
	
	public Car driveTo(RoutePoint target) {
		LOGGER.debug("Driving to {}", target);
		engine.nextRoutePoint(target);
		return this;
	}

	public String getModel() {
		return model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public UUID getDriverId() {
		return driverId;
	}
	
	
}

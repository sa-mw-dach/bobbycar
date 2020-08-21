package com.redhat.bobbycar.carsim.cars;

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

	private Car(Builder builder) {
		this.model = builder.model;
		this.manufacturer = builder.manufacturer;
		this.driverId = builder.driverId;
		this.engine = builder.engine;
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

	/**
	 * Creates builder to build {@link Car}.
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private String model;
		private String manufacturer;
		private UUID driverId;
		private Engine engine;

		private Builder() {
		}

		public Builder withModel(String model) {
			this.model = model;
			return this;
		}

		public Builder withManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
			return this;
		}

		public Builder withDriverId(UUID driverId) {
			this.driverId = driverId;
			return this;
		}
		
		public Builder withEngine(Engine engine) {
			this.engine = engine;
			return this;
		}

		public Car build() {
			return new Car(this);
		}
	}
	
	
}

package com.redhat.bobbycar.carsim.cars;

import java.util.Optional;
import java.util.UUID;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineMetrics {

	private static final Logger LOGGER = LoggerFactory.getLogger(EngineMetrics.class);
	private Optional<EngineData> engineData;

	public EngineMetrics(MetricRegistry registry, UUID driverId, String routeName) {
		super();
		String routeNameOrUnknown = (routeName != null && routeName.trim().length() > 0) ? routeName : "unknown";
		LOGGER.debug("Register metrics with tags driver='{}' and route='{}'", driverId, routeName);
		register("com.redhat.bobbycar.carsim.car.speed", () -> engineData.map(EngineData::getSpeedInKmh).orElse(0.0),
				"kmh", registry, driverId, routeNameOrUnknown);
		register("com.redhat.bobbycar.carsim.car.rpm", () -> engineData.map(EngineData::getRpm).orElse(0.0), "rpm",
				registry, driverId, routeNameOrUnknown);
		register("com.redhat.bobbycar.carsim.car.co2", () -> engineData.map(EngineData::getCo2Emission).orElse(0.0),
				"g/km", registry, driverId, routeNameOrUnknown);
		register("com.redhat.bobbycar.carsim.car.fuel",
				() -> engineData.map(EngineData::getFuelConsumptionPer100km).orElse(0.0), "l/100km", registry, driverId,
				routeNameOrUnknown);
		register("com.redhat.bobbycar.carsim.car.gear", () -> engineData.map(EngineData::getGear).orElse(0), "",
				registry, driverId, routeNameOrUnknown);
	}

	private void register(String name, Gauge<?> gauge, String unit, MetricRegistry registry, UUID driverId,
			String routeName) {
		Metadata metadata = Metadata.builder().withName(name).withType(MetricType.GAUGE).withUnit(unit).build();
		registry.register(metadata, gauge, new Tag("driver", driverId.toString()), new Tag("route", routeName));
	}

	public void updateEngineData(EngineData data) {
		LOGGER.debug("Updated engine data: {}", data);
		engineData = Optional.ofNullable(data);
	}

}

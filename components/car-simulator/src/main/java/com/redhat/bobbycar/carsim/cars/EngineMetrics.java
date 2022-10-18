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

	private static final String METRIC_FUEL_UNIT = "lper100km";
	private static final String METRIC_CO2_UNIT = "gPerKm";
	private static final String METRIC_KM_UNIT = "kmh";
	private static final String TAG_ROUTE = "route";
	private static final String TAG_DRIVER = "driver";
	private static final String METRIC_GEAR = "com.redhat.bobbycar.carsim.car.gear";
	private static final String METRIC_FUEL = "com.redhat.bobbycar.carsim.car.fuel";
	private static final String METRIC_CO2 = "com.redhat.bobbycar.carsim.car.co2";
	private static final String METRIC_RPM = "com.redhat.bobbycar.carsim.car.rpm";
	private static final String METRIC_CAR_SPEED = "com.redhat.bobbycar.carsim.car.speed";
	private static final Logger LOGGER = LoggerFactory.getLogger(EngineMetrics.class);
	private Optional<EngineData> engineData;

	public EngineMetrics(MetricRegistry registry, String driverId, String routeName) {
		super();
		String routeNameOrUnknown = (routeName != null && routeName.trim().length() > 0) ? routeName : "unknown";
		LOGGER.debug("Register metrics with tags driver='{}' and route='{}'", driverId, routeName);
		register(METRIC_CAR_SPEED, () -> engineData
				.map(EngineData::getSpeedInKmh)
				.orElse(0.0),
				METRIC_KM_UNIT, registry, driverId, routeNameOrUnknown);
		register(METRIC_RPM, () -> engineData
				.map(EngineData::getRpm)
				.orElse(0.0), 
				"", registry, driverId, routeNameOrUnknown);
		register(METRIC_CO2, () -> engineData
				.map(EngineData::getCo2Emission)
				.orElse(0.0),
				METRIC_CO2_UNIT, registry, driverId, routeNameOrUnknown);
		register(METRIC_FUEL,
				() -> engineData
				.map(EngineData::getFuelConsumptionPer100km)
				.orElse(0.0), 
				METRIC_FUEL_UNIT, registry, driverId, routeNameOrUnknown);
		register(METRIC_GEAR, () -> engineData
				.map(EngineData::getGear)
				.orElse(0), 
				"", registry, driverId, routeNameOrUnknown);
	}

	private void register(String name, Gauge<?> gauge, String unit, MetricRegistry registry, String driverId,
			String routeName) {
		Metadata metadata = Metadata.builder().withName(name).withType(MetricType.GAUGE).withUnit(unit).build();
		registry.register(metadata, gauge, 
				new Tag(TAG_DRIVER, driverId.toString()), 
				new Tag(TAG_ROUTE, routeName));
	}

	public void updateEngineData(EngineData data) {
		LOGGER.debug("Updated engine data: {}", data);
		engineData = Optional.ofNullable(data);
	}

}

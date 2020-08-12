package com.redhat.bobbycar.carsim.cars;

import java.util.Optional;

public interface EngineConfiguration {

	Optional<Double> maxSpeed();

	Optional<Double> co2FromSpeed(double speed);

	Optional<Double> fuelConsumptionPer100KmFromSpeed(double speed);

	Optional<Double> rpmFromSpeed(double speed);

	Optional<Integer> gearFromSpeed(double speed);

}
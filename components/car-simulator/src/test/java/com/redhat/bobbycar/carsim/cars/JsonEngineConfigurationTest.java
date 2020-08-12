package com.redhat.bobbycar.carsim.cars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.util.Map;

import javax.json.bind.JsonbException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonEngineConfigurationTest {
	
	@ParameterizedTest
	@ValueSource(doubles = {11.0, 25, 50.0, 65.0, 80.0, 85.0, 120.0, 150.0, 300})
	void testGearWithSpeed(double speed) throws JsonbException, FileNotFoundException {
		Map<Double, Integer> expectedGears = Map.of(11.0, 1, 25.0, 1, 50.0, 2, 65.0, 3, 80.0, 3, 85.0, 4, 120.0, 5, 150.0, 5, 300.0, -1);
		EngineConfiguration cfg = new JsonEngineConfiguration();
		assertEquals(expectedGears.get(speed), cfg.gearFromSpeed(speed).orElse(-1));
	}
	
	@ParameterizedTest
	@ValueSource(doubles = {11.0, 25, 50.0, 65.0, 80.0, 85.0, 120.0, 150.0, 300})
	void testRpmWithSpeed(double speed) throws JsonbException, FileNotFoundException {
		Map<Double, Double> expectedRpms = Map.of(11.0, 965.0, 25.0, 2193.0, 50.0, 3317.0, 65.0, 3198.0, 80.0, 3936.0, 85.0, 2958.0, 120.0, 3317.0, 150.0, 4146.0, 300.0, -1.0);
		EngineConfiguration cfg = new JsonEngineConfiguration();
		assertEquals(expectedRpms.get(speed), cfg.rpmFromSpeed(speed).orElse(-1.0), 0.5);
	}
	
	@ParameterizedTest
	@ValueSource(doubles = {11.0, 25, 50.0, 65.0, 80.0, 85.0, 120.0, 150.0, 300})
	void testFuelConsumptionWithSpeed(double speed) throws JsonbException, FileNotFoundException {
		Map<Double, Double> expectedRpms = Map.of(11.0, 3.0, 25.0, 3.0, 50.0, 3.5, 65.0, 4.4, 80.0, 5.2, 85.0, 5.5, 120.0, 7.5, 150.0, 9.2, 300.0, -1.0);
		EngineConfiguration cfg = new JsonEngineConfiguration();
		assertEquals(expectedRpms.get(speed), cfg.fuelConsumptionPer100KmFromSpeed(speed).orElse(-1.0), 0.5);
	}
}

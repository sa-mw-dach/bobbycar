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
		JsonEngineConfiguration cfg = new JsonEngineConfiguration();
		assertEquals(expectedGears.get(speed), cfg.gearFromSpeed(speed).orElse(-1));
	}
}

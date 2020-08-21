package com.redhat.bobbycar.carsim.cars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.redhat.bobbycar.carsim.drivers.RouteNotSupportedException;
import com.redhat.bobbycar.carsim.routes.RoutePoint;

class TimedEngineTest {
	
	private EngineConfiguration config;
	
	@BeforeEach
	void setUp() throws FileNotFoundException {
		config = new JsonEngineConfiguration();
	}
	
	
	@Test
	void testUnsupportedStartingPoint() {
		
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8));
		assertThrows(RouteNotSupportedException.class, () -> {
			TimedEngine.builder().withSpeedVariationInKmH(0).withStartingPoint(p1).withConfig(config).build();
	    });
	}
	
	@Test
	void testUnsupportedNextPoint() {
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8), ZonedDateTime.parse("2020-05-14T07:37:42Z"));
		RoutePoint p2 = new RoutePoint(new BigDecimal(8.270075), new BigDecimal(50.22086), new BigDecimal(269.6));
		Engine engine = TimedEngine.builder().withSpeedVariationInKmH(0).withStartingPoint(p1).withConfig(config).build();
		assertThrows(RouteNotSupportedException.class, () -> {
			engine.nextRoutePoint(p2);
	    });
	}
	
	@Test
	void testSpeedWithoutNextpoint() {
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8), ZonedDateTime.parse("2020-05-14T07:37:42Z"));
		Engine engine = TimedEngine.builder().withSpeedVariationInKmH(0).withStartingPoint(p1).withConfig(config).build();
		assertEquals(0, engine.currentData().getSpeedInKmh());
	}
	
	@Test
	void testSpeedWithoutVariation() {
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8), ZonedDateTime.parse("2020-05-14T07:37:42Z"));
		RoutePoint p2 = new RoutePoint(new BigDecimal(8.270075), new BigDecimal(50.22086), new BigDecimal(269.6), ZonedDateTime.parse("2020-05-14T07:37:59Z"));
		Engine engine = TimedEngine.builder().withSpeedVariationInKmH(0).withStartingPoint(p1).withConfig(config).build();
		assertEquals(5.19318504337, engine.nextRoutePoint(p2).currentData().getSpeedInKmh(), 5);
	}
	
	
}

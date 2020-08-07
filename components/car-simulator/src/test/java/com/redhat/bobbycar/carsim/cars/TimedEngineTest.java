package com.redhat.bobbycar.carsim.cars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.redhat.bobbycar.carsim.drivers.RouteNotSupportedException;
import com.redhat.bobbycar.carsim.routes.RoutePoint;

class TimedEngineTest {
	
	
	@Test
	void testUnsupportedStartingPoint() {
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8));
		assertThrows(RouteNotSupportedException.class, () -> {
			new TimedEngine(0, p1);
	    });
	}
	
	@Test
	void testUnsupportedNextPoint() {
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8), ZonedDateTime.parse("2020-05-14T07:37:42Z"));
		RoutePoint p2 = new RoutePoint(new BigDecimal(8.270075), new BigDecimal(50.22086), new BigDecimal(269.6));
		Engine engine = new TimedEngine(0, p1);
		assertThrows(RouteNotSupportedException.class, () -> {
			engine.nextRoutePoint(p2);
	    });
	}
	
	@Test
	void testSpeedWithoutNextpoint() {
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8), ZonedDateTime.parse("2020-05-14T07:37:42Z"));
		Engine engine = new TimedEngine(0, p1);
		assertEquals(0, engine.currentSpeedInKmH());
	}
	
	@Test
	void testSpeedWithoutVariation() {
		RoutePoint p1 = new RoutePoint(new BigDecimal(8.269912), new BigDecimal(50.22101), new BigDecimal(268.8), ZonedDateTime.parse("2020-05-14T07:37:42Z"));
		RoutePoint p2 = new RoutePoint(new BigDecimal(8.270075), new BigDecimal(50.22086), new BigDecimal(269.6), ZonedDateTime.parse("2020-05-14T07:37:59Z"));
		Engine engine = new TimedEngine(0, p1);
		assertEquals(5.19318504337, engine.nextRoutePoint(p2).currentSpeedInKmH(), 5);
	}
	
	
}

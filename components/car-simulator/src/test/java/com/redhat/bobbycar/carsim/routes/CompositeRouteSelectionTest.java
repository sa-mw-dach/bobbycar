package com.redhat.bobbycar.carsim.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CompositeRouteSelectionTest {

	@Test
	void testEmptyRouteSelectorHasNoRoutes() {
		CompositeRouteSelector routeSelector = new CompositeRouteSelector();
		assertEquals(0, routeSelector.routes());
	}
	
	@Test
	void testSelectRouteFromEmptyRouteSelector() {
		CompositeRouteSelector routeSelector = new CompositeRouteSelector();
		assertThrows(RouteSelectionException.class, () -> {
			routeSelector.selectRoute();
	    });
	}
	
	@Test
	void testSelectNonExistingRouteFromEmptyRouteSelector() {
		CompositeRouteSelector routeSelector = new CompositeRouteSelector();
		assertThrows(RouteSelectionException.class, () -> {
			routeSelector.selectRoute(20);
	    });
	}
	
	@Test
	void testSingleRouteSelectorHasOneRoute() {
		int routes = 5;
		RouteSelectionStrategy mock1 = Mockito.mock(RouteSelectionStrategy.class);
		Mockito.when(mock1.routes()).thenReturn(routes);
		CompositeRouteSelector routeSelector = new CompositeRouteSelector(mock1);
		assertEquals(routes, routeSelector.routes());
	}
	
	@Test
	void testSelectRouteFromSingleRouteSelector() {
		Route expectedRoute = new Route("A");
		RouteSelectionStrategy mock1 = Mockito.mock(RouteSelectionStrategy.class);
		Mockito.when(mock1.routes()).thenReturn(5);
		Mockito.when(mock1.selectRoute(Mockito.anyInt())).thenReturn(expectedRoute);
		CompositeRouteSelector routeSelector = new CompositeRouteSelector(mock1);
		assertEquals(expectedRoute, routeSelector.selectRoute());
	}
	
	@Test
	void testSelectAllRoutesFromSingleRouteSelector() {
		int routes = 5;
		Route expectedRoute = new Route("A");
		RouteSelectionStrategy mock1 = Mockito.mock(RouteSelectionStrategy.class);
		Mockito.when(mock1.routes()).thenReturn(routes);
		Mockito.when(mock1.selectRoute(Mockito.anyInt())).thenReturn(expectedRoute);
		CompositeRouteSelector routeSelector = new CompositeRouteSelector(mock1);
		for (int i = 0; i < routes; i++) {
			assertEquals(expectedRoute, routeSelector.selectRoute(i), "Route " + i + " cannot be selected");
		}
	}
	
	@Test
	void testSelectRoute() {
		int routes = 5;
		Route expectedRoute1 = new Route("A");
		RouteSelectionStrategy mock1 = Mockito.mock(RouteSelectionStrategy.class);
		Mockito.when(mock1.routes()).thenReturn(routes);
		Mockito.when(mock1.selectRoute(Mockito.anyInt())).thenReturn(expectedRoute1);
		Route expectedRoute2 = new Route("B");
		RouteSelectionStrategy mock2 = Mockito.mock(RouteSelectionStrategy.class);
		Mockito.when(mock2.routes()).thenReturn(routes);
		Mockito.when(mock2.selectRoute(Mockito.anyInt())).thenReturn(expectedRoute2);
		CompositeRouteSelector routeSelector = new CompositeRouteSelector(mock1, mock2);
		for (int i = 0; i < routes * 2; i++) {
			if (i < 5) {
				assertEquals(expectedRoute1, routeSelector.selectRoute(i), "Route " + i + " cannot be selected");
			} else {
				assertEquals(expectedRoute2, routeSelector.selectRoute(i), "Route " + i + " cannot be selected");
			}
		}
	}
}

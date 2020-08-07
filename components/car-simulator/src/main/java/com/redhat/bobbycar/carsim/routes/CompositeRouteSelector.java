package com.redhat.bobbycar.carsim.routes;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeRouteSelector implements RouteSelectionStrategy{
	private static final Logger LOGGER = LoggerFactory.getLogger(CompositeRouteSelector.class);
	private final RouteSelectionStrategy[] routeSelectionStrategies;
	private final int routes;
	private Random random = new Random();
	
	public CompositeRouteSelector(RouteSelectionStrategy... strategies) {
		routeSelectionStrategies = strategies;
		int counter = 0;
		for (int i = 0; i < strategies.length; i++) {
			counter += strategies[i].routes();
		}
		routes = counter;
	}
	
	public CompositeRouteSelector(Collection<RouteSelectionStrategy> strategies) {
		this(strategies.toArray(new RouteSelectionStrategy[] {}));
	}
	
	@Override
	public Route selectRoute() {
		if (routes > 0) {
			return selectRoute(random.nextInt(routes));
		} else {
			throw new RouteSelectionException("No route available");
		}
	}

	@Override
	public Route selectRoute(int route) {
		LOGGER.debug("Select route {}", route);
		int offset = 0;
		Optional<Route> selectedRoute = Optional.empty();
		for (int i = 0; offset <= route && i < routeSelectionStrategies.length; i++) {
			LOGGER.debug("Offset is {}", offset);
			if (route < routeSelectionStrategies[i].routes() + offset) {
				LOGGER.debug("Checking whether selection from strategy {} is possible", i);
				selectedRoute = Optional.ofNullable(routeSelectionStrategies[i].selectRoute(route -  offset));
				selectedRoute.ifPresent(s -> LOGGER.info("Route selected"));
			} else {
				LOGGER.debug("Next route strategy");
			}
			offset += routeSelectionStrategies[i].routes();
		}
		return selectedRoute.orElseThrow(() -> new RouteSelectionException("Route " + route + " not available"));
	}

	@Override
	public int routes() {
		return routes;
	}

}

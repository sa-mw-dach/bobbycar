package com.redhat.bobbycar.carsim.drivers;

import com.redhat.bobbycar.carsim.routes.Route;

public class RouteNotSupportedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Route route;
	
	public RouteNotSupportedException(String message, Route route) {
		super(message);
		this.route = route;
	}

	public Route getRoute() {
		return route;
	}

}

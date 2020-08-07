package com.redhat.bobbycar.carsim.routes;

public class RouteSelectionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RouteSelectionException() {
		super();
	}

	public RouteSelectionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RouteSelectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RouteSelectionException(String message) {
		super(message);
	}

	public RouteSelectionException(Throwable cause) {
		super(cause);
	}
	
}

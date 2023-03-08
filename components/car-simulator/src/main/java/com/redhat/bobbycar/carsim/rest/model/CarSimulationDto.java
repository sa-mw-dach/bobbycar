package com.redhat.bobbycar.carsim.rest.model;

import java.time.ZonedDateTime;

public class CarSimulationDto {

	private String id;
	private ZonedDateTime start;
	private ZonedDateTime end;
	private CarDto car;
	private RouteDto route;
	
	public CarSimulationDto() {
		
	}

	public CarSimulationDto(String id, ZonedDateTime start, ZonedDateTime end, CarDto car, RouteDto route) {
		super();
		this.id = id;
		this.start = start;
		this.end = end;
		this.car = car;
		this.route = route;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CarDto getCar() {
		return car;
	}

	public void setCar(CarDto car) {
		this.car = car;
	}

	public RouteDto getRoute() {
		return route;
	}

	public void setRoute(RouteDto route) {
		this.route = route;
	}

	public ZonedDateTime getStart() {
		return start;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public ZonedDateTime getEnd() {
		return end;
	}

	public void setEnd(ZonedDateTime end) {
		this.end = end;
	}

}

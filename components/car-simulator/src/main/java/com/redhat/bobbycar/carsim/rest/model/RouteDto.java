package com.redhat.bobbycar.carsim.rest.model;

public class RouteDto {
	private String name;

	public RouteDto() {
	}
	
	public RouteDto(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}

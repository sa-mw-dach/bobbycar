package com.redhat.bobbycar.carsim.rest.model;

public class CarDto {
	private String vin;
	
	public CarDto() {

	}

	public CarDto(String vin) {
		super();
		this.vin = vin;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}
	
	
}

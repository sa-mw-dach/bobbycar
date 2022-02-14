package com.redhat.bobbycar.carsim.clients.model;

import java.time.ZonedDateTime;

import javax.json.bind.annotation.JsonbProperty;

public class KafkaCarPosition {
	
	@JsonbProperty("lat")
	private double latitude;
	@JsonbProperty("long")
	private double longitude;
	@JsonbProperty("elev")
	private double elevation;
	@JsonbProperty("carid")
	private String carId;
	@JsonbProperty("vin")
	private String vin;
	
	private long eventTime;
	
	public KafkaCarPosition() {
		
	}
	
	public KafkaCarPosition(double latitude, double longitude, double elevation, String carId, ZonedDateTime time, String vin) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.eventTime = time.toInstant().toEpochMilli();
		this.carId = carId;
		this.vin = vin;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getElevation() {
		return elevation;
	}
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	public long getEventTime() {
		return eventTime;
	}
	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
}

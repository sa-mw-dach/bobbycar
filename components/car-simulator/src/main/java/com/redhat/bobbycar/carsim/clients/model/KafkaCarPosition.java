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
	
	private long eventTime;
	
	public KafkaCarPosition() {
		
	}
	
	public KafkaCarPosition(double latitude, double longitude, double elevation, ZonedDateTime time) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.eventTime = time.toInstant().toEpochMilli();
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
	
}

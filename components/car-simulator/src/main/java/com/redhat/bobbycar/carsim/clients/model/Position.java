package com.redhat.bobbycar.carsim.clients.model;

public class Position {
	private double lat;
	private double lng;

	public Position() {
		super();
	}

	public Position(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
package com.redhat.bobbycar.carsim.clients.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Position {
	double lat;
	double lng;

	public Position() {
		super();
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
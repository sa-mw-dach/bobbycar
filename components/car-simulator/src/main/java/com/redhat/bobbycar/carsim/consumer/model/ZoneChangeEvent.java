package com.redhat.bobbycar.carsim.consumer.model;

import java.io.Serializable;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ZoneChangeEvent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String previousZoneId;
	String nextZoneId;
	String carId;
	
	public ZoneChangeEvent() {
		super();
	}

	public ZoneChangeEvent(String carId, ZoneChangePayload payload) {
		super();
		this.carId = carId;
		this.previousZoneId = payload.previousZoneId;
		this.nextZoneId = payload.nextZoneId;
	}

	public ZoneChangeEvent(String previousZoneId, String nextZoneId, String carId) {
		super();
		this.previousZoneId = previousZoneId;
		this.nextZoneId = nextZoneId;
		this.carId = carId;
	}

	public String getPreviousZoneId() {
		return previousZoneId;
	}
	public void setPreviousZoneId(String previousZoneId) {
		this.previousZoneId = previousZoneId;
	}
	public String getNextZoneId() {
		return nextZoneId;
	}
	public void setNextZoneId(String nextZoneId) {
		this.nextZoneId = nextZoneId;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	@Override
	public int hashCode() {
		return Objects.hash(carId, nextZoneId, previousZoneId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoneChangeEvent other = (ZoneChangeEvent) obj;
		return Objects.equals(carId, other.carId) && Objects.equals(nextZoneId, other.nextZoneId)
				&& Objects.equals(previousZoneId, other.previousZoneId);
	}
	@Override
	public String toString() {
		return String.format("ZoneChangeEvent [previousZoneId=%s, nextZoneId=%s, carId=%s]", previousZoneId, nextZoneId,
				carId);
	}
}

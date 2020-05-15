package com.redhat.bobbycar.carsim.rest.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import com.redhat.bobbycar.carsim.CarEvent;

public class CarEventDto {
	private BigDecimal longitude;
	private BigDecimal latitude;
	private BigDecimal elevation;
	private Optional<ZonedDateTime> time;
	
	public CarEventDto() {
		
	}
	
	public CarEventDto(CarEvent evt) {
		this.longitude = evt.getLongitude();
		this.latitude = evt.getLatitude();
		this.elevation = evt.getElevation();
		this.time = evt.getTime();
	}
	
	public CarEventDto(BigDecimal longitude, BigDecimal latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.time = Optional.empty();
	}
	
	public BigDecimal getElevation() {
		return elevation;
	}

	public void setElevation(BigDecimal elevation) {
		this.elevation = elevation;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public Optional<ZonedDateTime> getTime() {
		return time;
	}

	public void setTime(Optional<ZonedDateTime> time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		return Objects.hash(elevation, latitude, longitude);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarEventDto other = (CarEventDto) obj;
		return Objects.equals(elevation, other.elevation) && Objects.equals(latitude, other.latitude)
				&& Objects.equals(longitude, other.longitude);
	}

	@Override
	public String toString() {
		return String.format("CarEventDto [longitude=%s, latitude=%s, elevation=%s]", longitude, latitude, elevation);
	}
}

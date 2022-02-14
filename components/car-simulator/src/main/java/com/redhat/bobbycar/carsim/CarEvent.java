package com.redhat.bobbycar.carsim;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

public class CarEvent {
	
	private final BigDecimal longitude;
	private final BigDecimal latitude; 
	private final BigDecimal elevation; 
	private final Optional<ZonedDateTime> time;
	private final String vin;
	
	public CarEvent(BigDecimal longitude, BigDecimal latitude, BigDecimal elevation, Optional<ZonedDateTime> time, String vin) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.elevation = elevation;
		this.time = time;
		this.vin = vin;
	}
	
	public BigDecimal getElevation() {
		return elevation;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public Optional<ZonedDateTime> getTime() {
		return time;
	}

	public String getVin() { return vin;}

	@Override
	public int hashCode() {
		return Objects.hash(elevation, latitude, longitude, time, vin);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarEvent other = (CarEvent) obj;
		return Objects.equals(elevation, other.elevation) && Objects.equals(latitude, other.latitude)
				&& Objects.equals(longitude, other.longitude) && Objects.equals(time, other.time) && Objects.equals(vin, other.vin);
	}

	@Override
	public String toString() {
		return String.format("CarEvent [longitude=%s, latitude=%s, elevation=%s, time=%s]", longitude, latitude,
				elevation, time);
	}
}

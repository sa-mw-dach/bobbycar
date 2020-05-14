package com.redhat.bobbycar.carsim;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

public class CarEvent {
	
	private final BigDecimal longitude;
	private final BigDecimal latitude; 
	private final Optional<ZonedDateTime> time;
	
	public CarEvent(BigDecimal longitude, BigDecimal latitude, Optional<ZonedDateTime> time) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.time = time;
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

	@Override
	public int hashCode() {
		return Objects.hash(latitude, longitude, time);
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
		return Objects.equals(latitude, other.latitude) && Objects.equals(longitude, other.longitude)
				&& Objects.equals(time, other.time);
	}

	@Override
	public String toString() {
		return String.format("CarEvent [longitude=%s, latitude=%s, time=%s]", longitude, latitude, time);
	}
}

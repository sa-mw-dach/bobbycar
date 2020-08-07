package com.redhat.bobbycar.carsim.routes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

public class RoutePoint implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final BigDecimal longitude;
	private final BigDecimal latitude;
	private final BigDecimal elevation;
	private final Optional<ZonedDateTime> time;
	
	public RoutePoint(BigDecimal longitude, BigDecimal latitude, BigDecimal elevation) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.elevation = elevation;
		this.time = Optional.empty();
	}

	public RoutePoint(BigDecimal longitude, BigDecimal latitude, BigDecimal elevation, ZonedDateTime time) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.elevation = elevation;
		this.time = Optional.ofNullable(time);
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
	
	public Optional<Duration> durationBetween(RoutePoint other) {
		return this.time.flatMap(t1 -> other.getTime().map(t2 -> Duration.between(t1, t2)));
	}
	
	public double distanceInMetersTo(RoutePoint other) {
		double lat1 = this.getLatitude().doubleValue();
		double lon1 = this.getLatitude().doubleValue();
		double lat2 = other.getLatitude().doubleValue();
		double lon2 = other.getLatitude().doubleValue();			
		
		int R = 6371000; // metres
		double phi1 = lat1 * Math.PI/180; // φ, λ in radians
		double phi2 = lat2 * Math.PI/180;
		double deltaPhi = (lat2-lat1) * Math.PI/180;
		double deltaLambda = (lon2-lon1) * Math.PI/180;

		double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
		          Math.cos(phi1) * Math.cos(phi2) *
		          Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

		return R * c; // in metres
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
		RoutePoint other = (RoutePoint) obj;
		return Objects.equals(latitude, other.latitude) && Objects.equals(longitude, other.longitude)
				&& Objects.equals(time, other.time);
	}

	@Override
	public String toString() {
		return String.format("RoutePoint [longitude=%s, latitude=%s, time=%s]", longitude, latitude, time);
	}
	
}

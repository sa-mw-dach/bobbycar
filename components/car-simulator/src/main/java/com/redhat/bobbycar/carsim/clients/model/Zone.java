package com.redhat.bobbycar.carsim.clients.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Zone implements Comparable<Zone> {
	private Metadata metadata;
	private ZoneSpec spec;

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metaData) {
		this.metadata = metaData;
	}

	public ZoneSpec getSpec() {
		return spec;
	}

	public void setSpec(ZoneSpec spec) {
		this.spec = spec;
	}

	public boolean isInside(double longitude, double latitude) {
		return distanceTo(longitude, latitude) <= spec.getRadius();
	}

	private double distanceTo(double longitude, double latitude) {
		double lat1 = spec.getPosition().getLat();
		double lon1 = spec.getPosition().getLng();
		double lat2 = latitude;
		double lon2 = longitude;

		int R = 6371000; // metres
		double phi1 = lat1 * Math.PI / 180; // φ, λ in radians
		double phi2 = lat2 * Math.PI / 180;
		double deltaPhi = (lat2 - lat1) * Math.PI / 180;
		double deltaLambda = (lon2 - lon1) * Math.PI / 180;

		double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
				+ Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return R * c; // in metres

	}

	@Override
	public int compareTo(Zone o) {
		return Integer.compare(this.getSpec().getPriority(), o.getSpec().getPriority());
	}

	@Override
	public int hashCode() {
		return Objects.hash(metadata);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Zone other = (Zone) obj;
		return Objects.equals(metadata, other.metadata);
	}

	@Override
	public String toString() {
		return String.format("Zone [metadata=%s, spec=%s]", metadata, spec);
	}
}

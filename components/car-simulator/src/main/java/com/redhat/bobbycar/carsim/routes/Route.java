package com.redhat.bobbycar.carsim.routes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Route {
	
	private final List<RoutePoint> points = new ArrayList<>();
	private String name;
	
	public Route(String name, List<RoutePoint> points) {
		this.points.addAll(points);
		this.name = name;
	}
	
	public Route(String name, RoutePoint... points) {
		this.points.addAll(Arrays.asList(points));
		this.name = name;
	}

	public Stream<RoutePoint> getPoints() {
		return points.stream();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, points);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		return Objects.equals(name, other.name) && Objects.equals(points, other.points);
	}

	@Override
	public String toString() {
		return String.format("Route [name=%s]", name);
	}

}

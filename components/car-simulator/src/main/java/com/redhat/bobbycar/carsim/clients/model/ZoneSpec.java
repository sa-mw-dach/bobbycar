package com.redhat.bobbycar.carsim.clients.model;

import java.util.Objects;

public class ZoneSpec {
	String name;
	Position position;
	int priority;
	int radius;
	String type;

	public ZoneSpec() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, position, priority, radius, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoneSpec other = (ZoneSpec) obj;
		return Objects.equals(name, other.name) && Objects.equals(position, other.position)
				&& priority == other.priority && radius == other.radius && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return String.format("ZoneSpec [name=%s, position=%s, priority=%s, radius=%s, type=%s]", name, position,
				priority, radius, type);
	}
}
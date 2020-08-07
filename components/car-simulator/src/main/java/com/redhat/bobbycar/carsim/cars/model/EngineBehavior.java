package com.redhat.bobbycar.carsim.cars.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EngineBehavior {
	
	private List<GearBehavior> gearBehaviors = new ArrayList<>();

	public List<GearBehavior> getGearBehaviors() {
		return gearBehaviors;
	}

	public void setGearBehaviors(List<GearBehavior> gearBehaviors) {
		this.gearBehaviors = gearBehaviors;
	}

	@Override
	public int hashCode() {
		return Objects.hash(gearBehaviors);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EngineBehavior other = (EngineBehavior) obj;
		return Objects.equals(gearBehaviors, other.gearBehaviors);
	}

	@Override
	public String toString() {
		return String.format("EngineBehavior [gearBehaviors=%s]", gearBehaviors);
	}
	
	
}

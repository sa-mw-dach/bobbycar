package com.redhat.bobbycar.carsim.cars.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GearBehavior {
	
	private int gear;
	private List<SpeedPerRpm> speedPerRpms = new ArrayList<>();
	public int getGear() {
		return gear;
	}
	public void setGear(int gear) {
		this.gear = gear;
	}
	public List<SpeedPerRpm> getSpeedPerRpms() {
		return speedPerRpms;
	}
	public void setSpeedPerRpms(List<SpeedPerRpm> speedPerRpms) {
		this.speedPerRpms = speedPerRpms;
	}
	@Override
	public int hashCode() {
		return Objects.hash(gear, speedPerRpms);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GearBehavior other = (GearBehavior) obj;
		return gear == other.gear && Objects.equals(speedPerRpms, other.speedPerRpms);
	}
	@Override
	public String toString() {
		return String.format("GearBehavior [gear=%s, speedPerRpms=%s]", gear, speedPerRpms);
	}
	
	
}

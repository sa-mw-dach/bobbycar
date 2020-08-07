package com.redhat.bobbycar.carsim.cars.model;

import java.util.Objects;

public class SpeedPerRpm {
	private double rpm;
	private double speed;
	
	public double getRpm() {
		return rpm;
	}
	public void setRpm(double rpm) {
		this.rpm = rpm;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	@Override
	public int hashCode() {
		return Objects.hash(rpm, speed);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpeedPerRpm other = (SpeedPerRpm) obj;
		return Double.doubleToLongBits(rpm) == Double.doubleToLongBits(other.rpm)
				&& Double.doubleToLongBits(speed) == Double.doubleToLongBits(other.speed);
	}
	@Override
	public String toString() {
		return String.format("SpeedPerRpm [rpm=%s, speed=%s]", rpm, speed);
	}
	
	
}

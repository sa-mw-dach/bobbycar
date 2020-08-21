package com.redhat.bobbycar.carsim.cars;

import java.io.Serializable;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class EngineData implements Serializable{
	private static final long serialVersionUID = 1L;
	private double speedInKmh;
	private double rpm;
	private int gear;
	private double fuelConsumptionPer100km;
	private double co2Emission;
	
	public EngineData() {
		
	}
	
	private EngineData(Builder builder) {
		this.speedInKmh = builder.speedInKmh;
		this.rpm = builder.rpm;
		this.gear = builder.gear;
		this.fuelConsumptionPer100km = builder.fuelConsumptionPer100km;
		this.co2Emission = builder.co2Emission;
	}
	
	public double getSpeedInKmh() {
		return speedInKmh;
	}

	public void setSpeedInKmh(double speedInKmh) {
		this.speedInKmh = speedInKmh;
	}

	public double getRpm() {
		return rpm;
	}

	public void setRpm(double rpm) {
		this.rpm = rpm;
	}

	public int getGear() {
		return gear;
	}

	public void setGear(int gear) {
		this.gear = gear;
	}

	public double getFuelConsumptionPer100km() {
		return fuelConsumptionPer100km;
	}

	public void setFuelConsumptionPer100km(double fuelConsumptionPer100km) {
		this.fuelConsumptionPer100km = fuelConsumptionPer100km;
	}

	public double getCo2Emission() {
		return co2Emission;
	}

	public void setCo2Emission(double co2Emission) {
		this.co2Emission = co2Emission;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	@Override
	public String toString() {
		return String.format("EngineData [speedInKmh=%s, rpm=%s, gear=%s, fuelConsumptionPer100km=%s, co2Emission=%s]",
				speedInKmh, rpm, gear, fuelConsumptionPer100km, co2Emission);
	}

	@Override
	public int hashCode() {
		return Objects.hash(co2Emission, fuelConsumptionPer100km, gear, rpm, speedInKmh);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EngineData other = (EngineData) obj;
		return Double.doubleToLongBits(co2Emission) == Double.doubleToLongBits(other.co2Emission)
				&& Double.doubleToLongBits(fuelConsumptionPer100km) == Double
						.doubleToLongBits(other.fuelConsumptionPer100km)
				&& gear == other.gear && Double.doubleToLongBits(rpm) == Double.doubleToLongBits(other.rpm)
				&& Double.doubleToLongBits(speedInKmh) == Double.doubleToLongBits(other.speedInKmh);
	}

	public static final class Builder {
		private double speedInKmh;
		private double rpm;
		private int gear;
		private double fuelConsumptionPer100km;
		private double co2Emission;

		private Builder() {
		}

		public Builder withSpeedInKmh(double speedInKmh) {
			this.speedInKmh = speedInKmh;
			return this;
		}

		public Builder withRpm(double rpm) {
			this.rpm = rpm;
			return this;
		}

		public Builder withGear(int gear) {
			this.gear = gear;
			return this;
		}

		public Builder withFuelConsumptionPer100km(double fuelConsumptionPer100km) {
			this.fuelConsumptionPer100km = fuelConsumptionPer100km;
			return this;
		}

		public Builder withCo2Emission(double co2Emission) {
			this.co2Emission = co2Emission;
			return this;
		}

		public EngineData build() {
			return new EngineData(this);
		}
	}
	
}

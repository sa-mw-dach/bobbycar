package com.redhat.bobbycar.model;

import java.util.Objects;

public class EngineData {

    private static final long serialVersionUID = 1L;
    private double speedInKmh;
    private double rpm;
    private int gear;
    private double fuelConsumptionPer100km;
    private double co2Emission;

    public EngineData() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineData that = (EngineData) o;
        return Double.compare(that.speedInKmh, speedInKmh) == 0 && Double.compare(that.rpm, rpm) == 0 && gear == that.gear && Double.compare(that.fuelConsumptionPer100km, fuelConsumptionPer100km) == 0 && Double.compare(that.co2Emission, co2Emission) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(speedInKmh, rpm, gear, fuelConsumptionPer100km, co2Emission);
    }

    @Override
    public String toString() {
        return "EngineData{" +
                "speedInKmh=" + speedInKmh +
                ", rpm=" + rpm +
                ", gear=" + gear +
                ", fuelConsumptionPer100km=" + fuelConsumptionPer100km +
                ", co2Emission=" + co2Emission +
                '}';
    }
}

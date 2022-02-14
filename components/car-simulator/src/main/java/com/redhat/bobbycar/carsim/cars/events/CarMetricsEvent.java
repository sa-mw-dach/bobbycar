package com.redhat.bobbycar.carsim.cars.events;

import java.io.Serializable;
import java.util.Objects;

import com.redhat.bobbycar.carsim.cars.Car;
import com.redhat.bobbycar.carsim.cars.EngineData;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CarMetricsEvent implements Serializable{
	private static final long serialVersionUID = 1L;
	private EngineData engineData;
	private String model;
	private String manufacturer;
	private String driverId;
	private String vin;

	public CarMetricsEvent() {

	}

	public CarMetricsEvent(EngineData engineData, String model, String manufacturer, String driverId, String vin) {
		super();
		this.engineData = engineData;
		this.model = model;
		this.manufacturer = manufacturer;
		this.driverId = driverId;
		this.vin = vin;
	}

	public static CarMetricsEvent create(Car car, EngineData currentData) {
		return new CarMetricsEvent(currentData, car.getModel(), car.getManufacturer(), car.getDriverId().toString(), car.getVin());
	}

	public EngineData getEngineData() {
		return engineData;
	}

	public void setEngineData(EngineData engineData) {
		this.engineData = engineData;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	@Override
	public int hashCode() {
		return Objects.hash(driverId, engineData, manufacturer, model, vin);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarMetricsEvent other = (CarMetricsEvent) obj;
		return Objects.equals(driverId, other.driverId) && Objects.equals(engineData, other.engineData)
				&& Objects.equals(manufacturer, other.manufacturer)  && Objects.equals(vin, other.vin);
	}

	@Override
	public String toString() {
		return String.format("EngineMetricsEvent [engineData=%s, model=%s, manufacturer=%s, driverId=%s, vin=%s]", engineData,
				model, manufacturer, driverId, vin);
	}
}

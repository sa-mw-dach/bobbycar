package com.redhat.bobbycar.model;

import java.io.Serializable;
import java.util.Objects;


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
    public String toString() {
        return "CarMetricsEvent{" +
                "engineData=" + engineData +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", driverId='" + driverId + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarMetricsEvent that = (CarMetricsEvent) o;
        return engineData.equals(that.engineData) && model.equals(that.model) && manufacturer.equals(that.manufacturer) && driverId.equals(that.driverId) && vin.equals(that.vin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(engineData, model, manufacturer, driverId, vin);
    }
}


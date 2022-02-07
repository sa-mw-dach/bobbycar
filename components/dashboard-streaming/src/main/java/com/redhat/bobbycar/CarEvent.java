package com.redhat.bobbycar;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CarEvent implements Comparable<CarEvent>{
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("long")
    private double longitude;
    @JsonProperty("elev")
    private double elevation;
    @JsonProperty("carid")
    private String carId;
    private long eventTime;

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getElevation() {
        return elevation;
    }
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }
    public String getCarId() {
        return carId;
    }
    public void setCarId(String carId) {
        this.carId = carId;
    }
    public long getEventTime() {
        return eventTime;
    }
    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
    @Override
    public int hashCode() {
        return Objects.hash(carId);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CarEvent other = (CarEvent) obj;
        return Objects.equals(carId, other.carId);
    }
    @Override
    public int compareTo(CarEvent o) {
        int result = carId.compareTo(o.getCarId());
        if (result == 0) {
            result = Long.compare(eventTime, o.getEventTime());
        }
        return result;
    }
    @Override
    public String toString() {
        return String.format("CarEvent [latitude=%s, longitude=%s, elevation=%s, carId=%s, eventTime=%s]",
                latitude, longitude, elevation, carId, eventTime);
    }
}

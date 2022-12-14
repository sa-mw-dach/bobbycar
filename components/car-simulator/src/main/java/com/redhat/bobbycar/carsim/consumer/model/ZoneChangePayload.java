package com.redhat.bobbycar.carsim.consumer.model;

import java.io.Serializable;
import java.util.Objects;

public class ZoneChangePayload implements Serializable {

    private static final long serialVersionUID = 1L;

    String previousZoneId;

    String nextZoneId;

    public ZoneChangePayload() {
        super();
    }

    public ZoneChangePayload(String previousZoneId, String nextZoneId, String carId) {
        super();
        this.previousZoneId = previousZoneId;
        this.nextZoneId = nextZoneId;
    }

    public String getPreviousZoneId() {
        return previousZoneId;
    }

    public void setPreviousZoneId(String previousZoneId) {
        this.previousZoneId = previousZoneId;
    }

    public String getNextZoneId() {
        return nextZoneId;
    }

    public void setNextZoneId(String nextZoneId) {
        this.nextZoneId = nextZoneId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nextZoneId, previousZoneId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ZoneChangePayload other = (ZoneChangePayload) obj;
        return Objects.equals(nextZoneId, other.nextZoneId)
                && Objects.equals(previousZoneId, other.previousZoneId);
    }

    @Override
    public String toString() {
        return String.format("ZoneChangeEvent [previousZoneId=%s, nextZoneId=%s]", previousZoneId, nextZoneId);
    }

}
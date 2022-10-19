package com.redhat.bobbycar.funqy.cloudevent;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ZoneChangeEvent {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String previousZoneId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nextZoneId;
    private String carId;

    public ZoneChangeEvent(){};

    public ZoneChangeEvent(String previousZoneId, String nextZoneId, String carId) {
        super();

        this.previousZoneId = previousZoneId;
        this.nextZoneId = nextZoneId;
        this.carId = carId;
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

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}

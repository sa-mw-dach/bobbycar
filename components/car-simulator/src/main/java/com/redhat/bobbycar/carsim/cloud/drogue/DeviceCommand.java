package com.redhat.bobbycar.carsim.cloud.drogue;

import com.google.common.base.MoreObjects;

public class DeviceCommand<T> {
    private final String device;

    private final T payload;

    public DeviceCommand(String device, T payload) {
        this.device = device;
        this.payload = payload;
    }

    public String getDevice() {
        return device;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("device", device)
                .add("payload", payload)
                .toString();
    }
}
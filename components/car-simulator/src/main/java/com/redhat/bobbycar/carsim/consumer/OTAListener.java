package com.redhat.bobbycar.carsim.consumer;

public interface OTAListener {
    public void onUpdate(String event);
    public String getVin();
}
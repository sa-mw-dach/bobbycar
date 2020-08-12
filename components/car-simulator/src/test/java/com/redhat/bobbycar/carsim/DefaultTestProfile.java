package com.redhat.bobbycar.carsim;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class DefaultTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() { 
        return Map.of("com.redhat.bobbycar.carsim.route", "src/test/resources/gps/gpx/test", 
        		"com.redhat.bobbycar.carsim.kafka.url", "http://localhost",
        		"com.redhat.bobbycar.carsim.datagrid.url", "http://localhost"
        		);
    }

    @Override
    public String getConfigProfile() { 
        return "test";
    }
}

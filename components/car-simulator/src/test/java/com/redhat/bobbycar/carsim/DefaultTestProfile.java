package com.redhat.bobbycar.carsim;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class DefaultTestProfile implements QuarkusTestProfile {
	
	@Override
	public Map<String, String> getConfigOverrides() {
		Map<String, String> configOverrides = new HashMap<>();
		configOverrides.putAll(Map.of("com.redhat.bobbycar.carsim.route", "src/test/resources/gps/gpx/test",
				"quarkus.http.test-port", "0"
				));
		configOverrides.putAll(Map.of(
			"mp.messaging.incoming.zonechange.topic", "bobbycar/zonechange",
			"mp.messaging.incoming.zonechange.type", "smallrye-mqtt",
			"mp.messaging.incoming.zonechange.connector", "smallrye-mqtt-server"
		));
			
		configOverrides.putAll(Map.of(
			"mp.messaging.outgoing.zonechangepub.topic", "bobbycar/zonechange",
			"mp.messaging.outgoing.zonechangepub.type", "smallrye-mqtt",
			"mp.messaging.outgoing.zonechangepub.host", "localhost",
			"mp.messaging.outgoing.zonechangepub.port", "1883"
		));
		return configOverrides;
	}

	@Override
	public String getConfigProfile() {
		return "test";
	}

}

package com.redhat.bobbycar.carsim.cloud.drogue.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.Json;

public class SerializeTest {

    @Test
    public void testSerializeJackson() {

        var metadata = new ScopedMetadata();
        metadata.name = "device";
        metadata.application = "application";
        var device = new Device();
        device.setMetadata(metadata);

        Map<String, Object> spec = Map.of("authentication", Map.of("credentials", new Object[] {
                Map.of("pass", "foo")
        }));
        device.setSpec(spec);

        var json = Json.encode(device);

        assertEquals("{\"metadata\":{\"name\":\"device\",\"application\":\"application\"},\"spec\":{\"authentication\":{\"credentials\":[{\"pass\":\"foo\"}]}}}", json);

    }

    @Test
    public void testSerializeJsonb() {

        var metadata = new ScopedMetadata();
        metadata.name = "device";
        metadata.application = "application";
        var device = new Device();
        device.setMetadata(metadata);

        Map<String, Object> spec = Map.of("authentication", Map.of("credentials", new Object[] {
                Map.of("pass", "foo")
        }));
        device.setSpec(spec);

        var json = JsonbBuilder.create().toJson(device);

        assertEquals("{\"metadata\":{\"application\":\"application\",\"name\":\"device\"},\"spec\":{\"authentication\":{\"credentials\":[{\"pass\":\"foo\"}]}}}", json);

    }
}

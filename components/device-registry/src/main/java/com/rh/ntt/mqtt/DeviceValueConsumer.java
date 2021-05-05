package com.rh.ntt.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rh.ntt.model.DeviceValueReport;
import org.bson.Document;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeviceValueConsumer {

    @Inject
    ObjectMapper objectMapper;

    @Incoming("device-value")
    // @Outgoing("stream")
    public String processDeviceValues(byte[] payload) throws JsonProcessingException {
        String deviceValue = new String(payload);
        Document doc = Document.parse(deviceValue);
        System.out.println("Payload report: " + doc.get("report"));
        DeviceValueReport dvr = objectMapper.readValue(deviceValue, DeviceValueReport.class);
        System.out.println("Receiving device value: " + dvr);
        return deviceValue;
    }
}

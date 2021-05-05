package com.rh.ntt.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rh.ntt.db.MongoService;
import com.rh.ntt.model.AddGatewayReport;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@ApplicationScoped
public class GatewayReportConsumer {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    MongoService mongoService;
    @Inject
    Validator validator;

    @Incoming("gateway-report")
    public void processGatewayReport(byte[] payload) throws JsonProcessingException {
        String gatewayReport = new String(payload);
        System.out.println("Receiving GW_REPORT Payload: ");
        try {
            AddGatewayReport gwReport = objectMapper.readValue(gatewayReport, AddGatewayReport.class);
            Set<ConstraintViolation<AddGatewayReport>> violations = validator.validate(gwReport);
            if (violations.isEmpty()) {
                System.out.println("Persisting gateway-report: " + gwReport);
                mongoService.addGatewayReport(gwReport);
                mongoService.deleteGatewayAnnounceById(mongoService.findGatewayIdByUUID(gwReport.getGateway_UUID()));
                mongoService.deleteGatewayMappingByUUID(gwReport.getGateway_UUID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.rh.ntt.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rh.ntt.db.MongoService;
import com.rh.ntt.model.GatewayAnnounce;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;


@ApplicationScoped
public class GatewayAnnounceConsumer {

    private final Set<String> gatewayAnnouncesCache = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

    @Inject
    ObjectMapper objectMapper;
    @Inject
    MongoService mongoService;
    @Inject
    Validator validator;

    @Incoming("gateway-announce")
    public void processGatewayAnnounce(byte[] payload) throws JsonProcessingException {
        String gw = new String(payload);
        try {
            GatewayAnnounce an = objectMapper.readValue(gw, GatewayAnnounce.class);
            System.out.println("Receiving gateway-announce with gwId: " + an.getGateway_id());
            Set<ConstraintViolation<GatewayAnnounce>> violations = validator.validate(an);
            if (violations.isEmpty() && !this.gatewayAnnouncesCache.contains(an.getGateway_id())) {
                mongoService.addGatewayAnnounce(an);
                this.gatewayAnnouncesCache.add(an.getGateway_id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

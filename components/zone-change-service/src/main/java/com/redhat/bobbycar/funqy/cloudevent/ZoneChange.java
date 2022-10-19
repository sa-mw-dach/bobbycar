package com.redhat.bobbycar.funqy.cloudevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.bobbycar.funqy.rest.OtaCampaignService;
import io.quarkus.funqy.Context;
import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class ZoneChange {

    private static final Logger log = Logger.getLogger(ZoneChange.class);

    @RestClient
    OtaCampaignService campaignService;

    @Funq
    // @CloudEventMapping(trigger = "annotated", responseSource = "annotated", responseType = "lastChainLink")
    public void zoneChange(ZoneChangeEvent zoneChange, @Context CloudEvent cloudEvent) {

        if (cloudEvent != null) {
            log.info(
                    "CloudEvent {" +
                            "id='" + cloudEvent.id() + '\'' +
                            ", specVersion='" + cloudEvent.specVersion() + '\'' +
                            ", source='" + cloudEvent.source() + '\'' +
                            ", subject='" + cloudEvent.subject() + '\'' +
                            ", time='" + cloudEvent.time() + '\'' +
                            '}');
            log.info(
                    "ZoneChange {" +
                            "carid='" + zoneChange.getCarId() + '\'' +
                            ", nextZone='" + zoneChange.getNextZoneId() + '\'' +
                            ", previousZone='" + zoneChange.getPreviousZoneId() + '\'' +
                            '}');

            if(zoneChange.getNextZoneId() != null) {
                System.out.println("Assigning vehicle OTA campaign....");
                Response response = campaignService.assignVinToCampaign(zoneChange.getCarId(), zoneChange.getNextZoneId());
                log.info(response);
            }

        }

    }
}

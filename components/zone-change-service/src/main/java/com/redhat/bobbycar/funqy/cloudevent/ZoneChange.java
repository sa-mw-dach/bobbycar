package com.redhat.bobbycar.funqy.cloudevent;

import io.quarkus.funqy.Context;
import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventMapping;
import org.jboss.logging.Logger;

public class ZoneChange {

    private static final Logger log = Logger.getLogger(ZoneChange.class);

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
        }
        log.info(
                "process ZoneChangeEvent {" +
                    "carId='" + zoneChange.getCarId() + '\'' +
                    ", previousZoneId='" + zoneChange.getPreviousZoneId() + '\'' +
                    ", nextZoneId='" + zoneChange.getNextZoneId() + '\'' +
                    '}');
    }
}

package com.redhat.bobbycar.ota;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.bobbycar.ota.model.EngineBehavior;
import com.redhat.bobbycar.ota.mqtt.MQTTService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/ota")
public class OTAResource {

    @Inject
    CampaignService campaignService;
    @Inject
    MQTTService mqttService;
    @Inject
    ObjectMapper objectMapper;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response index() {
        return Response.ok("Bobbycar OTA example server").build();
    }

    @GET
    @Path("/assign/{vin}/campaign/{campaign-id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignVinToCampaign(@PathParam("vin") String vin, @PathParam("campaign-id") String campaignid){
        System.out.println("Assigning vin: "+vin+" to campaign: "+campaignid);
        EngineBehavior result = campaignService.getPayloadById(campaignid);
        if (result != null){
            System.out.println("Triggering OTA Update....");
            try {
                mqttService.sendMessage(objectMapper.writeValueAsString(result), "ota/"+vin, 0);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return Response.ok().build();
    }

    @POST
    @Path("/payload/{campaign-id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCampaignPayload(@PathParam("campaign-id") String campaignid, EngineBehavior payload){
        System.out.println("Creating OTA payload for campaign: " + campaignid);
        campaignService.addCampaign(campaignid, payload);
        return Response.ok(payload).build();
    }

    @GET
    @Path("/payload/{campaign-id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCampaignPayload(@PathParam("campaign-id") String campaignid){
        System.out.println("Reading OTA payload for campaign: " + campaignid);
        EngineBehavior result = campaignService.getPayloadById(campaignid);
        if(result == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(result).build();
    }
}
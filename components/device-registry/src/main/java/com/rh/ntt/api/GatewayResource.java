package com.rh.ntt.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rh.ntt.db.MongoService;
import com.rh.ntt.model.*;
import com.rh.ntt.mqtt.MQTTPaho;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/gateways")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GatewayResource {

    @ConfigProperty(name = "mqtt.paho.topic.add-gateway", defaultValue = "MSAPGatewayPublic")
    String addGatewayTopic;
    @ConfigProperty(name = "mqtt.paho.topic.announce-gateway", defaultValue = "MSAPPublic")
    String gatewayAnnounceTopic;
    @ConfigProperty(name = "mqtt.paho.topic.report-gateway", defaultValue = "MSAPGatewayReport")
    String gatewayReportTopic;
    @ConfigProperty(name = "mqtt.paho.topic.device-value", defaultValue = "MSAPDeviceValue")
    String deviceValueTopic;

    @Inject
    MQTTPaho mqttProducer;
    @Inject
    MongoService mongoService;
    @Inject
    ObjectMapper objectMapper;

    /*
    @Inject
    @Channel("stream")
    Publisher<String> deviceValues;
    */

    @POST
    @Path("/register/{gatewayId}")
    public Response registerGateway(@PathParam("gatewayId") @NotEmpty String gatewayId) {
        AddGateway gw = new AddGateway();
        gw.setGateway_id(gatewayId);
        try {
            mqttProducer.sendMessage(objectMapper.writeValueAsString(gw), addGatewayTopic, 0);
            mongoService.addGatewayMapping(gw);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.status(200).build();
    }

    @GET
    @Path("/announces")
    public Response listGateways() {
        return Response.status(200).entity(mongoService.listAllGatewayAnnounces()).build();
    }

    @POST
    @Path("/announces")
    public Response addGatewayAnnounce(@Valid GatewayAnnounce gwAnnounce) {
        try {
            mqttProducer.sendMessage(objectMapper.writeValueAsString(gwAnnounce), gatewayAnnounceTopic, 0);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.status(200).build();
    }

    @GET
    @Path("/announces/count")
    public Response countAnnounces() {
        return Response.status(200).entity(mongoService.countGatewayAnnounces()).build();
    }

    @DELETE
    @Path("/announces/{gatewayId}")
    public Response deleteAnnounce(@PathParam("gatewayId") String gatewayId) {
        mongoService.deleteGatewayAnnounceById(gatewayId);
        return Response.status(200).build();
    }

    @DELETE
    @Path("/announces/all")
    public Response deleteAllAnnounce() {
        mongoService.deleteAllGatewayAnnounces();
        return Response.status(200).build();
    }

    @POST
    @Path("/reports")
    public Response addGatewayReport(@Valid AddGatewayReport addGatewayReport) {
        try {
            mqttProducer.sendMessage(objectMapper.writeValueAsString(addGatewayReport), gatewayReportTopic, 0);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.status(200).build();
    }

    @POST
    @Path("/devices/values")
    public Response addDeviceValue(@Valid DeviceValueReport deviceValueReport) {
        try {
            mqttProducer.sendMessage(objectMapper.writeValueAsString(deviceValueReport), deviceValueTopic, 0);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.status(200).build();
    }

    @GET
    @Path("/devices/informations/{gateway_UUID}")
    public Response getDeviceValue(@PathParam("gateway_UUID") String gwUUID) {
        try {
            mqttProducer.sendMessage(objectMapper.writeValueAsString(new GetDevicesInformation()), gwUUID+"/device", 0);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.status(200).build();
    }

    /*
    @GET
    @Path("/devices/values/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> streamDeviceValues() {
        return deviceValues;
    }
    */
}
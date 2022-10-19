package com.redhat.bobbycar.funqy.rest;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/api/ota")
@RegisterRestClient
public interface OtaCampaignService {

    @GET
    @Path("/assign/{vin}/campaign/{campaign-id}")
    Response assignVinToCampaign(@PathParam("vin") String vin, @PathParam("campaign-id") String campaignid);
}
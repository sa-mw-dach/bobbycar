package com.redhat.bobbycar.restClient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/v1")
@RegisterRestClient(configKey="ibm-weather-api")
public interface IBMWeatherService {

    @GET
    @Path("/geocode/{lat}/{lon}/observations.json")
    Response getCurrentWeather(
            @PathParam("lat") double latitude,
            @PathParam("lon") double longitude,
            @QueryParam("language") String language,
            @QueryParam("units") String units,
            @QueryParam("apiKey") String apiKey);

}
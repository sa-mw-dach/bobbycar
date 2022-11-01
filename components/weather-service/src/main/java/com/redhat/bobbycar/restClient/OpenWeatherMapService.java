package com.redhat.bobbycar.restClient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/data/2.5")
@RegisterRestClient(configKey="weather-api")
public interface OpenWeatherMapService {

    @GET
    @Path("/weather")
    Response getCurrentWeather(
            @QueryParam("lat") double latitude,
            @QueryParam("lon") double longitude,
            @QueryParam("appid") String apiKey);

    @GET
    @Path("/air_pollution")
    Response getAirPollution(
            @QueryParam("lat") double latitude,
            @QueryParam("lon") double longitude,
            @QueryParam("appid") String apiKey);

}
package com.redhat.bobbycar;

import com.redhat.bobbycar.restClient.OpenWeatherMapService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/weather")
public class WeatherResource {

    private static final Logger log = Logger.getLogger(WeatherResource.class);

    @ConfigProperty(name = "open-weather-map.api.key")
    String apiKey;

    @RestClient
    OpenWeatherMapService weatherMapService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @GET
    @Path("/version")
    @Produces(MediaType.TEXT_PLAIN)
    public String version() {
        return "OpenWeatherMap V2.5";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/current/lat/{lat}/lon/{lon}")
    public Response getCurrentWeather(@PathParam("lat") double lat, @PathParam("lon") double lon) {
        Response response = weatherMapService.getCurrentWeather(lat, lon, apiKey);
        log.info(response);
        return response;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pollution/lat/{lat}/lon/{lon}")
    public Response getAirPollution(@PathParam("lat") double lat, @PathParam("lon") double lon) {
        Response response = weatherMapService.getAirPollution(lat, lon, apiKey);
        log.info(response);
        return response;
    }

}
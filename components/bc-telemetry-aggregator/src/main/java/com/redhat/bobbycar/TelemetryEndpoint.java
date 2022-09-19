package com.redhat.bobbycar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.redhat.bobbycar.model.Aggregation;

@ApplicationScoped
@Path("/telemetry")
public class TelemetryEndpoint {

    @Inject
    InteractiveQueries interactiveQueries;

    @GET
    public Response getDefaultResponse(){
        return Response.ok("Welcome to Kafka Streams Interactive Query").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/averages/{vin}")
    public Response getMetricAveragesByVin(@PathParam("vin") String vin) {
        Aggregation result = interactiveQueries.getCarMetricByVIN(vin);

        if (result != null) {
            System.out.println("Found Metric for VIN: " + vin);
            return Response.ok(result).build();
        }
        else {
            System.out.println("NOT FOUND Metric for VIN: " + vin);
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    "No data found for VIN " + vin).build();
        }
    }

}

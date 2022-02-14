package com.redhat.bobbycar.carsim.rest;

import com.redhat.bobbycar.carsim.cars.Car;
import com.redhat.bobbycar.carsim.cars.EngineConfiguration;
import com.redhat.bobbycar.carsim.cars.model.EngineBehavior;
import com.redhat.bobbycar.carsim.data.CarDao;
import com.redhat.bobbycar.carsim.data.DriverDao;
import com.redhat.bobbycar.carsim.rest.model.CarDto;
import com.redhat.bobbycar.carsim.rest.model.CarSimulationDto;
import com.redhat.bobbycar.carsim.rest.model.RouteDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/api/cars")
public class CarResource {

    @Inject
    CarDao carDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Car> getAll() {
        return carDao.getAllCars().stream().collect(Collectors.toList());
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByUUID(@PathParam("uuid") UUID uuid) {
        Optional<Car> car = carDao.getById(uuid);
        return Response.ok(car).build();
    }

    @POST
    @Path("/{uuid}/vin/{vin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignVin(@PathParam("uuid") UUID uuid, @PathParam("vin") String vin) {
        Optional<Car> car = carDao.getById(uuid);
        car.ifPresent(c -> {
            c.assignVin(vin);
        });
        return Response.ok(car).build();
    }
}

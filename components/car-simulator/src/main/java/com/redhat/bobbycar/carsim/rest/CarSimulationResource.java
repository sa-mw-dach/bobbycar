package com.redhat.bobbycar.carsim.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.bobbycar.carsim.data.DriverDao;
import com.redhat.bobbycar.carsim.rest.model.CarDto;
import com.redhat.bobbycar.carsim.rest.model.CarSimulationDto;
import com.redhat.bobbycar.carsim.rest.model.RouteDto;

@Path("/api/carsimulation")
public class CarSimulationResource {
	
	@Inject
	DriverDao driverDao;
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CarSimulationDto> getAll() {
		return driverDao.getAllDrivers().stream().map(d -> new CarSimulationDto(d.getId(), d.getStart(), d.getEnd(),
				new CarDto("12345678901234567"), new RouteDto(d.getRouteName()))).collect(Collectors.toList());
	}

}

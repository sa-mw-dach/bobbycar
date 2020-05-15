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
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.annotations.SseElementType;

import com.redhat.bobbycar.carsim.CarSimulatorApp;
import com.redhat.bobbycar.carsim.rest.model.CarDto;
import com.redhat.bobbycar.carsim.rest.model.CarEventDto;
import com.redhat.bobbycar.carsim.rest.model.CarSimulationDto;
import com.redhat.bobbycar.carsim.rest.model.RouteDto;

import io.smallrye.mutiny.Multi;

@Path("/api/carsimulation")
public class CarSimulationResource {
	
	@Inject
	CarSimulatorApp app;
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CarSimulationDto> getAll() {
		return app.getDrivers().stream().map(d -> new CarSimulationDto(d.getId(), d.getMetrics().getStart(), d.getMetrics().getEnd(),
				new CarDto("12345678901234567"), new RouteDto(d.getRouteName()))).collect(Collectors.toList());
	}
	
	@GET
	@Path("/{id}/route")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	@SseElementType(MediaType.APPLICATION_JSON)
	public Multi<CarEventDto> getRoute(@PathParam("id") UUID id) throws JAXBException {
		return Multi.createFrom().emitter(em -> 
			app.getDriver(id).registerCarEventListener(evt -> em.emit(new CarEventDto(evt)))
		);
	}
}

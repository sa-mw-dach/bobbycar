package com.redhat.bobbycar.carsim.clients;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.redhat.bobbycar.carsim.clients.model.Rss;

@Path("")
@RegisterRestClient(configKey = "osmtraces")
public interface OsmRoutesService {
	
	@GET
	@Path("/traces/rss")
	@Produces("application/xml; charset=utf-8")
	@Consumes("application/xml; charset=utf-8")
	public Rss getTraces();
	
	@GET
	@Path("/trace/{traceid}/data")
	@Produces("application/xml; charset=utf-8")
	@Consumes("application/xml; charset=utf-8")
	public String getRawTraceData(@PathParam("traceid") String traceid);
}

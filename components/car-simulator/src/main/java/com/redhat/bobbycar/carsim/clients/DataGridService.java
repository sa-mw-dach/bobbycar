package com.redhat.bobbycar.carsim.clients;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.redhat.bobbycar.carsim.clients.model.Zone;

@Path("")
@RegisterRestClient(configKey = "datagrid")
public interface DataGridService {
	
	/**
	 * 
	 * @param apiKey
	 * @param contentType Content Type is otherwise always set to application/octetstream
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/zones/{zoneid}")
	Zone getZoneData(@QueryParam("user_key") String apiKey, @HeaderParam("Content-Type") String contentType, @PathParam("zoneid") String zoneid);
}

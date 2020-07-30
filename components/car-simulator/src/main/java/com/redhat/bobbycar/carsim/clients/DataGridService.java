package com.redhat.bobbycar.carsim.clients;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("")
@RegisterRestClient(configKey = "datagrid")
public interface DataGridService {
	@GET
	@Path("/zones/{zoneid}")
	public String getZoneData(@QueryParam("user_key") String apiKey, @PathParam("zoneid") String zoneid);
}

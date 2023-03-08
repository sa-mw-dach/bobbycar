package com.redhat.bobbycar.carsim.clients;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.redhat.bobbycar.carsim.clients.model.KafkaCarEvent;

@Path("/topics/bobbycar-gps")
@RegisterRestClient(configKey = "kafka")
public interface KafkaService {

	@POST
	@Consumes("application/vnd.kafka.json.v2+json")
	@Produces("application/vnd.kafka.v2+json")
	public void publishCarEvent(@QueryParam("user_key") String apiKey, KafkaCarEvent carEvent);
}

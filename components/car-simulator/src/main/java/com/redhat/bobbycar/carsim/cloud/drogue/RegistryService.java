package com.redhat.bobbycar.carsim.cloud.drogue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpHeaders;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.redhat.bobbycar.carsim.Profiles;
import com.redhat.bobbycar.carsim.cloud.drogue.model.Device;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.oidc.client.filter.OidcClientFilter;

@IfBuildProfile(Profiles.DROGUE)
@Path("/api/registry/v1alpha1")
@RegisterRestClient(configKey = "drogue-device-registry")
@OidcClientFilter
public interface RegistryService {

    @GET
    @Path("/apps/{application}/devices/{device}")
    @Produces(MediaType.APPLICATION_JSON)
    Device getDevice(@PathParam("application") String application, @PathParam("device") String device);

    @POST
    @Path("/apps/{application}/devices")
    @Consumes(MediaType.APPLICATION_JSON)
    void createDevice(@PathParam("application") String application, Device device);

    @PUT
    @Path("/apps/{application}/devices/{device}")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateDevice(@PathParam("application") String application, @PathParam("device") String device, Device payload);

}

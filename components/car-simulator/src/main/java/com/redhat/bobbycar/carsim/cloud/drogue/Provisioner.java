package com.redhat.bobbycar.carsim.cloud.drogue;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.Profiles;
import com.redhat.bobbycar.carsim.cloud.drogue.model.Device;
import com.redhat.bobbycar.carsim.cloud.drogue.model.ScopedMetadata;

import io.quarkus.arc.profile.IfBuildProfile;
import io.vertx.core.json.Json;

@IfBuildProfile(Profiles.DROGUE)
@ApplicationScoped
public class Provisioner implements com.redhat.bobbycar.carsim.cloud.Provisioner {

    private static final Logger log = LoggerFactory.getLogger(Provisioner.class);

    @ConfigProperty(name = "drogue.application")
    String application;

    @ConfigProperty(name = "com.redhat.bobbycar.carsim.instance")
    String simulatorInstance;

    @ConfigProperty(name = "drogue.gateway.password")
    String gatewayPassword;

    @RestClient
    RegistryService registry;

    private String getGatewayName() {
        return String.format("%s-gw", this.simulatorInstance);
    }

    private Optional<Device> getDevice(String name) {
        try {
            return Optional.ofNullable(this.registry.getDevice(this.application, name));
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                return Optional.empty();
            }
            throw e;
        }

    }

    @PostConstruct
    public void ensureGateway() {
        var name = getGatewayName();

        log.info("Ensure that gateway is created: {}", name);

        ensureDevice(name, () -> Map.of("authentication",
                Map.of("credentials", new Object[] {
                        Map.of("pass", gatewayPassword)
                })));

    }

    public String provisionDevice(long index) {
        var name = String.format("%s-%s", this.simulatorInstance, index);

        log.info("Provision device: {} -> {}", index, name);

        ensureDevice(name, () -> {
            var gateway = getGatewayName();
            return Map.of("gatewaySelector",
                    Map.of("matchNames",
                            new Object[] { gateway }));
        });

        return name;
    }

    Device newDevice(String name) {
        var device = new Device();
        var metadata = new ScopedMetadata();
        metadata.setApplication(application);
        metadata.setName(name);
        device.setMetadata(metadata);
        return device;
    }

    void ensureDevice(String name, Supplier<Map<String, Object>> specSupplier) {
        var currentDevice = getDevice(name);
        log.info("Device({}): {}", name, currentDevice);
        if (currentDevice.isEmpty()) {
            var device = newDevice(name);
            device.setSpec(specSupplier.get());
            log.info("Creating new device: {}", Json.encodePrettily(device));
            this.registry.createDevice(application, device);
        }
    }
}

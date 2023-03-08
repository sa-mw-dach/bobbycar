package com.redhat.bobbycar.carsim.cloud.direct;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.bobbycar.carsim.Profiles;

import io.quarkus.arc.profile.UnlessBuildProfile;

@UnlessBuildProfile(Profiles.DROGUE)
@ApplicationScoped
public class Provisioner implements com.redhat.bobbycar.carsim.cloud.Provisioner {

    @Override
    public String provisionDevice(long index) {
        return UUID.randomUUID().toString();
    }

}

package com.redhat.bobbycar.carsim.cloud.drogue.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device {

    ScopedMetadata metadata;

    Map<String,Object> spec;

    Map<String,Object> status;

    public void setMetadata(ScopedMetadata metadata) {
        this.metadata = metadata;
    }

    public ScopedMetadata getMetadata() {
        return metadata;
    }

    public Map<String,Object> getSpec() {
        return spec;
    }

    public void setSpec(Map<String,Object> spec) {
        this.spec = spec;
    }

    public Map<String,Object> getStatus() {
        return status;
    }

    public void setStatus(Map<String,Object> status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("metadata", metadata)
                .add("spec", spec)
                .add("status", status)
                .toString();
    }
}

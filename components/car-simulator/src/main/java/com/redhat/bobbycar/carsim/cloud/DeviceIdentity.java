package com.redhat.bobbycar.carsim.cloud;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.errorprone.annotations.Immutable;

@Immutable
public class DeviceIdentity {
    private final String application;

    private final String device;

    private DeviceIdentity(final String application, final String device) {
        this.application = application;
        this.device = device;
    }

    public static DeviceIdentity of(String application, String device) {
        return new DeviceIdentity(
                Objects.requireNonNull(application),
                Objects.requireNonNull(device)
        );
    }

    public String getApplication() {
        return this.application;
    }

    public String getDevice() {
        return this.device;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("application", this.application)
                .add("device", this.device)
                .toString();
    }

    /**
     * Create an Authorization header for using with HTTP Basic Auth.
     *
     * @param password The password to use.
     */
    public String createAuthHeader(final String password) {
        var credentials = String.format("%s@%s:%s", URLEncoder.encode(this.device, StandardCharsets.UTF_8), this.application, password);
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}

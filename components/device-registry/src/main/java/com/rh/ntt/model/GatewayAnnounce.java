package com.rh.ntt.model;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;

public class GatewayAnnounce {

    @NotEmpty(message="Gateway ID may not be empty")
    private String gateway_id;
    @NotEmpty(message="Report may not be empty")
    private String report;
    private Date timestamp;
    private String domain;
    private String firmware_ver;

    public GatewayAnnounce() {
    }

    public GatewayAnnounce(String gateway_id, String report, Date timestamp, String domain, String firmware_ver) {
        this.gateway_id = gateway_id;
        this.report = report;
        this.timestamp = timestamp;
        this.domain = domain;
        this.firmware_ver = firmware_ver;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFirmware_ver() {
        return firmware_ver;
    }

    public void setFirmware_ver(String firmware_ver) {
        this.firmware_ver = firmware_ver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GatewayAnnounce that = (GatewayAnnounce) o;
        return gateway_id.equals(that.gateway_id) && report.equals(that.report) && timestamp.equals(that.timestamp) && Objects.equals(domain, that.domain) && Objects.equals(firmware_ver, that.firmware_ver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gateway_id, report, timestamp, domain, firmware_ver);
    }

    @Override
    public String toString() {
        return "GatewayAnnounce{" +
                "gateway_id='" + gateway_id + '\'' +
                ", report='" + report + '\'' +
                ", timestamp=" + timestamp +
                ", domain='" + domain + '\'' +
                ", firmware_ver='" + firmware_ver + '\'' +
                '}';
    }
}

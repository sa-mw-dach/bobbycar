package com.rh.ntt.model;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;

public class AddGatewayReport {

    @NotEmpty(message="Gateway UUID may not be empty")
    private String gateway_UUID;
    @NotEmpty(message="Report may not be empty")
    private String report;
    private Date timestamp = new Date();
    private Long msg_ID_ack = Long.valueOf(0);
    @NotEmpty(message="Status may not be empty")
    private String status;
    private String name;
    private String description;
    private String location;

    public AddGatewayReport(String gateway_UUID, String report, Date timestamp, Long msg_ID_ack, String status, String name, String description, String location) {
        this.gateway_UUID = gateway_UUID;
        this.report = report;
        this.timestamp = timestamp;
        this.msg_ID_ack = msg_ID_ack;
        this.status = status;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public AddGatewayReport() {
    }

    public String getGateway_UUID() {
        return gateway_UUID;
    }

    public void setGateway_UUID(String gateway_UUID) {
        this.gateway_UUID = gateway_UUID;
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

    public Long getMsg_ID_ack() {
        return msg_ID_ack;
    }

    public void setMsg_ID_ack(Long msg_ID_ack) {
        this.msg_ID_ack = msg_ID_ack;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddGatewayReport that = (AddGatewayReport) o;
        return gateway_UUID.equals(that.gateway_UUID) && report.equals(that.report) && Objects.equals(timestamp, that.timestamp) && Objects.equals(msg_ID_ack, that.msg_ID_ack) && status.equals(that.status) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gateway_UUID, report, timestamp, msg_ID_ack, status, name, description, location);
    }

    @Override
    public String toString() {
        return "AddGatewayReport{" +
                "gateway_UUID='" + gateway_UUID + '\'' +
                ", report='" + report + '\'' +
                ", timestamp=" + timestamp +
                ", msg_ID_ack=" + msg_ID_ack +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

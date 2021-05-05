package com.rh.ntt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class DeviceValueReport {

    @NotEmpty(message="Gateway UUID may not be empty")
    private String gateway_UUid;
    @NotEmpty(message="Report may not be empty")
    private String report;
    private Date timestamp;
    private String msg_ID_ack;
    private Long id;
    private BigDecimal value;

    public DeviceValueReport() {
    }

    public DeviceValueReport(String gateway_UUid, String report, Date timestamp, String msg_ID_ack, Long id, BigDecimal value) {
        this.gateway_UUid = gateway_UUid;
        this.report = report;
        this.timestamp = timestamp;
        this.msg_ID_ack = msg_ID_ack;
        this.id = id;
        this.value = value;
    }

    public String getGateway_UUid() {
        return gateway_UUid;
    }

    @JsonProperty("gateway_UUID")
    public void setGateway_UUid(String gateway_UUid) {
        this.gateway_UUid = gateway_UUid;
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

    public String getMsg_ID_ack() {
        return msg_ID_ack;
    }

    public void setMsg_ID_ack(String msg_ID_ack) {
        this.msg_ID_ack = msg_ID_ack;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceValueReport that = (DeviceValueReport) o;
        return gateway_UUid.equals(that.gateway_UUid) && report.equals(that.report) && Objects.equals(timestamp, that.timestamp) && Objects.equals(msg_ID_ack, that.msg_ID_ack) && id.equals(that.id) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gateway_UUid, report, timestamp, msg_ID_ack, id, value);
    }

    @Override
    public String toString() {
        return "DeviceValueReport{" +
                "gateway_UUid='" + gateway_UUid + '\'' +
                ", report='" + report + '\'' +
                ", timestamp=" + timestamp +
                ", msg_ID_ack='" + msg_ID_ack + '\'' +
                ", id=" + id +
                ", value=" + value +
                '}';
    }
}

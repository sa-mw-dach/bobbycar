package com.rh.ntt.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;

public class AddGateway {

    @NotEmpty(message="Gateway ID may not be empty")
    private String gateway_id;
    @NotEmpty(message="Command may not be empty")
    private String command = "sys_add_gateway";
    private Date timestamp = new Date();
    private Long msg_ID = Long.valueOf(0);
    @NotEmpty(message="UUID may not be empty")
    private String gateway_uuid = java.util.UUID.randomUUID().toString();

    public AddGateway(String gateway_id, String command, Date timestamp, Long msg_ID, String UUID) {
        this.gateway_id = gateway_id;
        this.command = command;
        this.timestamp = timestamp;
        this.msg_ID = msg_ID;
        this.gateway_uuid = UUID;
    }

    public AddGateway() {
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getMsg_ID() {
        return msg_ID;
    }

    public void setMsg_ID(Long msg_ID) {
        this.msg_ID = msg_ID;
    }

    public String getGateway_uuid() {
        return gateway_uuid;
    }

    @JsonProperty("gateway_uuid")
    public void setGateway_uuid(String UUID) {
        this.gateway_uuid = UUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddGateway that = (AddGateway) o;
        return gateway_id.equals(that.gateway_id) && command.equals(that.command) && Objects.equals(timestamp, that.timestamp) && Objects.equals(msg_ID, that.msg_ID) && gateway_uuid.equals(that.gateway_uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gateway_id, command, timestamp, msg_ID, gateway_uuid);
    }

    @Override
    public String toString() {
        return "AddGateway{" +
                "gateway_id='" + gateway_id + '\'' +
                ", command='" + command + '\'' +
                ", timestamp=" + timestamp +
                ", msg_ID='" + msg_ID + '\'' +
                ", gateway_UUID='" + gateway_uuid + '\'' +
                '}';
    }
}

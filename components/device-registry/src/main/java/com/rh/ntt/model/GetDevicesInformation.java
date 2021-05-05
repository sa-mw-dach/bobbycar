package com.rh.ntt.model;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;

public class GetDevicesInformation {

    private Long msg_ID = Long.valueOf(0);
    private String command = "get_devices_info";
    private Date timestamp = new Date();

    public GetDevicesInformation() {
    }

    public GetDevicesInformation(Long msg_ID, String command, Date timestamp) {
        this.msg_ID = msg_ID;
        this.command = command;
        this.timestamp = timestamp;
    }

    public Long getMsg_ID() {
        return msg_ID;
    }

    public void setMsg_ID(Long msg_ID) {
        this.msg_ID = msg_ID;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetDevicesInformation that = (GetDevicesInformation) o;
        return Objects.equals(msg_ID, that.msg_ID) && command.equals(that.command) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg_ID, command, timestamp);
    }

    @Override
    public String toString() {
        return "GetDevicesInformation{" +
                "msg_ID='" + msg_ID + '\'' +
                ", command='" + command + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

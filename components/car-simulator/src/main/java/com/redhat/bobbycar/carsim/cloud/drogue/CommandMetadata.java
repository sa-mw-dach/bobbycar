package com.redhat.bobbycar.carsim.cloud.drogue;

import java.util.Objects;
import java.util.Optional;

import com.google.common.base.MoreObjects;

class CommandMetadata {
    private final String device;

    private final String command;

    private CommandMetadata(final String device, final String command) {
        this.device = device;
        this.command = command;
    }

    public String getDevice() {
        return this.device;
    }

    public String getCommand() {
        return this.command;
    }

    public static CommandMetadata of(final String device, final String command) {
        return new CommandMetadata(
                Objects.requireNonNull(device),
                Objects.requireNonNull(command)
        );
    }

    public static Optional<CommandMetadata> fromCommandTopic(String topic) {
        var toks = topic.split("/", 4);
        if (toks.length == 4 && toks[0].equals("command") && toks[1].equals("inbox")) {
            return Optional.of(of(toks[2], toks[3]));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("device", device)
                .add("command", command)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommandMetadata that = (CommandMetadata) o;
        return Objects.equals(device, that.device)
                && Objects.equals(command, that.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                device,
                command
        );
    }
}
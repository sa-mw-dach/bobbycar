package com.redhat.bobbycar.carsim.cloud.drogue;

import static com.redhat.bobbycar.carsim.cloud.drogue.CommandMetadata.fromCommandTopic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MqttCommandsTest {

    @Test
    public void testTopicDevice() {
        var optMeta = fromCommandTopic("command/inbox/foo/bar");
        assertTrue(optMeta.isPresent());
        var meta = optMeta.get();
        assertEquals("foo", meta.getDevice());
        assertEquals("bar", meta.getCommand());
    }

    @Test
    public void testTopicGateway() {
        var optMeta = fromCommandTopic("command/inbox//bar");
        assertTrue(optMeta.isPresent());
        var meta = optMeta.get();
        assertEquals("", meta.getDevice());
        assertEquals("bar", meta.getCommand());
    }

    @Test
    public void testTopicDeviceLongCommand() {
        var optMeta = fromCommandTopic("command/inbox/foo/bar/baz");
        assertTrue(optMeta.isPresent());
        var meta = optMeta.get();
        assertEquals("foo", meta.getDevice());
        assertEquals("bar/baz", meta.getCommand());
    }

    @Test
    public void testTopicInvalid() {
        assertTrue(fromCommandTopic("command/inbox/").isEmpty());
        assertTrue(fromCommandTopic("command/").isEmpty());
    }

}
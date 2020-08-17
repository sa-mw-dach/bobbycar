package com.redhat.bobbycar.carsim.consumer;

import org.junit.jupiter.api.Disabled;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
@Disabled("Disabled because dependency injection does not work")
public class NativeZoneChangeConsumerIT extends ZoneChangeConsumerTest{

	public NativeZoneChangeConsumerIT(ZoneChangeConsumer consumer) {
		super(consumer);
	}

}

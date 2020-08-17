package com.redhat.bobbycar.carsim.consumer;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeZoneChangeConsumerIT extends ZoneChangeConsumerTest{

	public NativeZoneChangeConsumerIT(ZoneChangeConsumer consumer) {
		super(consumer);
	}

}

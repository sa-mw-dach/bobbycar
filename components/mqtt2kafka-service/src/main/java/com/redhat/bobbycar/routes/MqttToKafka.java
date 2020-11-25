package com.redhat.bobbycar.routes;

import java.nio.charset.Charset;
import java.util.stream.Collectors;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

public class MqttToKafka extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {

		from("paho:{{com.redhat.bobbycar.camelk.mqtt.topic}}?brokerUrl={{com.redhat.bobbycar.camelk.mqtt.brokerUrl}}")
			.log("Publishing ${body} to kafka")
		.to("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=mqtt2kafkaClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}");
	
	}	
	
}

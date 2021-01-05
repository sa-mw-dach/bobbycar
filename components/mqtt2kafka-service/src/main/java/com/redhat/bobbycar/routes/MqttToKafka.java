package com.redhat.bobbycar.routes;

import java.nio.charset.Charset;
import java.util.stream.Collectors;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;

public class MqttToKafka extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {

		from("paho:{{com.redhat.bobbycar.camelk.mqtt.topic}}?brokerUrl={{com.redhat.bobbycar.camelk.mqtt.brokerUrl}}")
			.log("Publishing engine metric ${body} to Kafka")
		.to("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=mqtt2kafkaClientEM&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}");

		from("paho:{{com.redhat.bobbycar.camelk.mqtt.topicZoneChange}}?brokerUrl={{com.redhat.bobbycar.camelk.mqtt.brokerUrl}}")
			.log("Publishing zone change ${body} to Kafka")
		.to("kafka:{{com.redhat.bobbycar.camelk.kafka.topicZoneChange}}?clientId=mqtt2kafkaClientZC&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}");
	
	}	
	
}

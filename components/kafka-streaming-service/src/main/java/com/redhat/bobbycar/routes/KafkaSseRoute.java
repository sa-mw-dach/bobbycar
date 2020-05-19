package com.redhat.bobbycar.routes;
import org.apache.camel.builder.RouteBuilder;

public class KafkaSseRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=kafkaSseCamelClient&brokers=bobbycar-cluster-kafka-brokers:9092")
			.log("Message received from Kafka : ${body}")
			.to("undertow:ws://0.0.0.0:8080/api/carevents?sendToAll=true");
		from("undertow:ws://0.0.0.0:8080/api/carevents?sendToAll=true")
			.log("Message received from Websocket : ${body}");
	}
	
	
}

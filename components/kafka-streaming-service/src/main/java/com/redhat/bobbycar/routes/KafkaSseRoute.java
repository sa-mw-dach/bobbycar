package com.redhat.bobbycar.routes;
import org.apache.camel.builder.RouteBuilder;

public class KafkaSseRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {

		// expose Bobbycar GPS positions from Kafka as Websocket
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic.gps}}?clientId=kafkaSseCamelClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}")
			.log("GPS position received from Kafka : ${body}")
			.to("undertow:ws://0.0.0.0:8080/api/carevents?sendToAll=true");
		from("undertow:ws://0.0.0.0:8080/api/carevents?sendToAll=true")
			.log("Message received from Websocket : ${body}");

		// expose Bobbycar engine metrics from Kafka as Websocket
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic.metrics}}?clientId=kafkaSseMetricsClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}")
			.log("Metric received from Kafka : ${body}")
			.to("undertow:ws://0.0.0.0:8080/api/carmetrics?sendToAll=true");
		from("undertow:ws://0.0.0.0:8080/api/carmetrics?sendToAll=true")
			.log("Message received from Websocket : ${body}");
		
	}
	
	
}

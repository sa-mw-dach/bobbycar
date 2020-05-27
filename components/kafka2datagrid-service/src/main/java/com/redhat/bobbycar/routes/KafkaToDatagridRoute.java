package com.redhat.bobbycar.routes;
import java.util.Properties;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class KafkaToDatagridRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		Properties properties = new Properties();
		properties.put("infinispan.client.hotrod.auth_username", propertyInject("{{com.redhat.bobbycar.camelk.dg.user}}", String.class));
		properties.put("infinispan.client.hotrod.auth_password", propertyInject("{{com.redhat.bobbycar.camelk.dg.password}}", String.class));
		ConfigurationBuilder hotRodBuilder = new ConfigurationBuilder();
		hotRodBuilder.addServer()
	        .host(propertyInject("{{com.redhat.bobbycar.camelk.dg.host}}", String.class))
	        .port(9999)
	        .withProperties(properties)
	        .version(org.infinispan.client.hotrod.ProtocolVersion.PROTOCOL_VERSION_30)
	    .build();
		
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=kafkaSseCamelClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}:9092")
			.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
		    .setHeader(InfinispanConstants.KEY).expression(simple("${headers[kafka.KEY]}"))
		    .setHeader(InfinispanConstants.VALUE).expression(simple("${body}"))
		    .log("Saving data to cache with key: ${headers[kafka.KEY]} and value: ${body}")
			.to("infinispan://{{com.redhat.bobbycar.camelk.dg.cacheName}}");
	}
	
	
}

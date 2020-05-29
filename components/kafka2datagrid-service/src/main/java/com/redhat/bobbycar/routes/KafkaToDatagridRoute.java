package com.redhat.bobbycar.routes;
import java.nio.charset.Charset;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.commons.marshall.StringMarshaller;

public class KafkaToDatagridRoute extends RouteBuilder {
	
	@PropertyInject("com.redhat.bobbycar.camelk.dg.host")
    private String datagridHost;
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.user", defaultValue = "developer")
    private String datagridUsername;
	@PropertyInject("com.redhat.bobbycar.camelk.dg.password")
    private String datagridPassword;
	
	@Override
	public void configure() throws Exception {
		bindToRegistry("cacheContainerConfiguration", getCacheConfig());
		
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=kafkaToDatagridCamelClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}:9092")
			.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
			.setHeader(InfinispanConstants.KEY).expression(jsonpath("$.carid"))
			/*.process((ex) -> {
				System.out.println(ex.getIn().getBody().getClass());
				ex.getIn().setBody(ex.getIn().getBody().toString());
				ex.getIn().setBody("{\"_type\":\"Car\"," + ex.getIn().getBody().toString().substring(1));
			})*/
		    .setHeader(InfinispanConstants.VALUE).expression(simple("${body}"))
		    
		    .log("Saving data to cache with key: ${headers[CamelInfinispanKey]} and value: ${body} of type  ${body.class}")
			.to("infinispan://{{com.redhat.bobbycar.camelk.dg.cacheName}}?cacheContainerConfiguration=#cacheContainerConfiguration");
	}
	
	private Configuration getCacheConfig() throws Exception {
		ConfigurationBuilder hotRodBuilder = new ConfigurationBuilder();

		return hotRodBuilder.addServer()
	        .host(datagridHost).port(11222)
	        	.marshaller(new StringMarshaller(Charset.defaultCharset()))
	        	//.marshaller(new ProtoStreamMarshaller())
	        .clientIntelligence(ClientIntelligence.BASIC)
	        	.security()
	        		.authentication().enable()
	        		.username(datagridUsername)
	        		.password(datagridPassword)
	        		.realm("default")
	        		.serverName("infinispan")
	        		.saslQop(SaslQop.AUTH)
	        		.saslMechanism("DIGEST-MD5")
	        		
			.ssl()
	        	.sniHostName(datagridHost)
	        	.trustStorePath("/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt")
        .build();
	}
	
}

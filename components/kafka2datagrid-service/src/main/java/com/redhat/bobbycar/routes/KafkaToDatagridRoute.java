package com.redhat.bobbycar.routes;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.apache.camel.processor.aggregate.DefaultAggregateController;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
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
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.aggregationInterval", defaultValue = "60000")
    private long aggregationInterval;
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.aggregationDistinct", defaultValue = "true")
    private boolean aggregationDistinct;
	
	public static class Entry {
		String key;
		String value;
		
		public Entry(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public int hashCode() {
			return Objects.hash(key);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			return Objects.equals(key, other.key);
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	@Override
	public void configure() throws Exception {
		bindToRegistry("cacheContainerConfiguration", getCacheConfig());
		
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=kafkaToDatagridCamelClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}:9092")
			.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
			.setHeader(InfinispanConstants.KEY).expression(jsonpath("$.carid"))
		    .setHeader(InfinispanConstants.VALUE).expression(simple("${body}"))
		    .log("Saving data to cache with key: ${headers[CamelInfinispanKey]} and value: ${body} of type  ${body.class}")
			.to("infinispan://{{com.redhat.bobbycar.camelk.dg.cacheName}}?cacheContainerConfiguration=#cacheContainerConfiguration");
		
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=kafkaToDatagridAggregatorCamelClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}:9092")
				.aggregate(simple("true"), new GroupedBodyAggregationStrategy())
				.completionInterval(aggregationInterval).id("myAggregator")
				.aggregateController(new DefaultAggregateController())
				.process(ex -> {
					@SuppressWarnings("unchecked")
					List<String> aggregation = ex.getIn().getBody(List.class);
					Stream<String> aggStream = aggregation.stream().sorted();
					if (aggregationDistinct) {
						aggStream = aggStream.map(s -> new Entry(s.trim().substring(9, 46), s)).distinct().map(Entry::toString);
					}
					ex.getIn().setBody("[" + aggStream.collect(Collectors.joining(",")) + "]");
				})
				.log("Aggregated for the given interval: ${body}")
				.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
				.setHeader(InfinispanConstants.KEY).expression(simple("aggregated"))
			    .setHeader(InfinispanConstants.VALUE).expression(simple("${body}"))
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

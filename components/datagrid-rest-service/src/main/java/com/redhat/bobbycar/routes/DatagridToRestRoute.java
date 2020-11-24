package com.redhat.bobbycar.routes;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.apache.camel.model.rest.RestBindingMode;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.commons.marshall.StringMarshaller;

public class DatagridToRestRoute extends RouteBuilder {
	// private static final String PATH_TO_SERVICE_CA = "/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt";
	@PropertyInject("com.redhat.bobbycar.camelk.dg.host")
    private String datagridHost;
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.user", defaultValue = "developer")
    private String datagridUsername;
	@PropertyInject("com.redhat.bobbycar.camelk.dg.password")
    private String datagridPassword;
	private RemoteCacheManager cacheManager;
	private RemoteCache<String, String> zonesCache;
	private RemoteCache<String, String> carsCache;
	
	@Override
	public void configure() throws Exception {
		
		restConfiguration().host("0.0.0.0").port(8080).component("undertow")
			//.enableCORS(true)
			//.corsAllowCredentials(true)
			//.corsHeaderProperty("Access-Control-Allow-Origin","*")
			//.corsHeaderProperty("Access-Control-Allow-Headers","Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
			.bindingMode(RestBindingMode.json)
			.dataFormatProperty("prettyPrint", "true")
			.contextPath("/");
		Configuration cacheConfig = createCacheConfig();
		bindToRegistry("cacheManager", cacheConfig);
		bindToRegistry("cacheContainerConfiguration", cacheConfig);
		initRemoteCache(cacheConfig);
		from("rest:get:cars")
			.setHeader("Access-Control-Allow-Origin",constant("*"))
			.process(ex -> {
				ex.getIn().setBody("[" + carsCache.values().stream().collect(Collectors.joining(",")) + "]");
			});
		from("rest:get:cars/{carid}")
			 .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
			 .setHeader(InfinispanConstants.KEY).expression(simple("${headers[carid]}"))
			.to("infinispan://{{com.redhat.bobbycar.camelk.dg.car.cacheName}}?cacheContainerConfiguration=#cacheContainerConfiguration");
		from("rest:get:zones")
			.setHeader("Access-Control-Allow-Origin",constant("*"))
			.process(ex -> {
				ex.getIn().setBody("[" + zonesCache.values().stream().collect(Collectors.joining(",")) + "]");
			});	
		from("rest:get:zones/{zoneid}")	
		 	.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
		 	.setHeader(InfinispanConstants.KEY).expression(simple("${headers[zoneid]}"))
		 	.to("infinispan://{{com.redhat.bobbycar.camelk.dg.zone.cacheName}}?cacheContainerConfiguration=#cacheContainerConfiguration");
	}

	private void initRemoteCache(Configuration cacheConfig) {
		cacheManager = new RemoteCacheManager(cacheConfig);
		cacheManager.start();
		zonesCache = cacheManager.administration().getOrCreateCache("zones", "default");
		carsCache = cacheManager.administration().getOrCreateCache("cars", "default");
		cacheManager.start();
	}
	
	private Configuration createCacheConfig() {
		ConfigurationBuilder hotRodBuilder = new ConfigurationBuilder();
		return hotRodBuilder.addServer()
	        .host(datagridHost).port(11222)
	        	.marshaller(new StringMarshaller(Charset.defaultCharset()))
	        .clientIntelligence(ClientIntelligence.BASIC)
	        	.security()
	        		.authentication().enable()
	        		.username(datagridUsername)
	        		.password(datagridPassword)
	        		.realm("default")
	        		.serverName("infinispan")
	        		.saslQop(SaslQop.AUTH)
	        		.saslMechanism("DIGEST-MD5")
        .build();
	}
	
	
}

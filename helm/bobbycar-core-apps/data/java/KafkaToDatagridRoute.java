package com.redhat.bobbycar.routes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.X509TrustManager;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.apache.camel.component.netty.http.NettyHttpMessage;
import org.apache.camel.model.OnCompletionDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.processor.aggregate.DefaultAggregateController;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.apache.camel.support.jsse.FilterParameters;
import org.apache.camel.support.jsse.SSLContextClientParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.apache.camel.util.json.JsonObject;
import org.apache.camel.util.json.Jsoner;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.commons.marshall.StringMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaToDatagridRoute extends RouteBuilder {

	private static final String ZONE_CHANGE_HEADER = "zoneChange";
	private static final String ZONE_PREV_HEADER = "previousZone";
	private static final String ZONE_NXT_HEADER = "nextZone";
	private static final String CAR_ID_HEADER = "carid";
	private static final String VIN_HEADER = "vin";
	private static final String CACHE_TEMPLATE = "org.infinispan.DIST_ASYNC";
	private static final String PATH_TO_SERVICE_CA = "/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt";
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaToDatagridRoute.class);
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
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.car.cacheName")
	private String carsCacheName;
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.car.snapshot.cacheName")
	private String carsnapshotCacheName;
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.zone.cacheName")
	private String zonesCacheName;
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.ocp.api")
	private String ocpAPIHost;
	@PropertyInject(value = "com.redhat.bobbycar.camelk.dg.namespace")
	private String namespace;

	@PropertyInject(value = "com.redhat.bobbycar.camelk.drogue.endpoint")
	private String drogueCommandEndpoint;

	@PropertyInject(value = "com.redhat.bobbycar.camelk.drogue.user")
	private String drogueCommandUser;

	@PropertyInject(value = "com.redhat.bobbycar.camelk.drogue.token")
	private String drogueCommandToken;

	@PropertyInject(value = "com.redhat.bobbycar.camelk.drogue.application")
	private String drogueApplication;

	@PropertyInject(value = "com.redhat.bobbycar.camelk.mode", defaultValue = "direct")
	private String applicationMode;

	private RemoteCacheManager cacheManager;
	private RemoteCache<String, String> zonesCache;
	private RemoteCache<String, String> carsCache;
	private RemoteCache<String, String> carsnapshotsCache;
	private ObjectMapper mapper = new ObjectMapper();

	public static class ZoneChangeEvent {
		private final String previousZoneId;
		private final String nextZoneId;
		private final String carId;
		private final String vin;

		public ZoneChangeEvent(String previousZoneId, String nextZoneId, String carId, String vin) {
			super();
			this.previousZoneId = previousZoneId;
			this.nextZoneId = nextZoneId;
			this.carId = carId;
			this.vin = vin;
		}
		public String getPreviousZoneId() {
			return previousZoneId;
		}
		public String getNextZoneId() {
			return nextZoneId;
		}
		public String getCarId() {
			return carId;
		}
		public String getVin() {
			return vin;
		}
	}

	public static class CarEvent implements Comparable<CarEvent>{
		@JsonProperty("lat")
		private double latitude;
		@JsonProperty("long")
		private double longitude;
		@JsonProperty("elev")
		private double elevation;
		@JsonProperty("carid")
		private String carId;
		@JsonProperty("vin")
		private String vin;
		private long eventTime;
		private Zone zone;

		public double getLatitude() {
			return latitude;
		}
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		public double getElevation() {
			return elevation;
		}
		public void setElevation(double elevation) {
			this.elevation = elevation;
		}
		public String getCarId() {
			return carId;
		}
		public void setCarId(String carId) {
			this.carId = carId;
		}
		public String getVin() {
			return vin;
		}
		public void setVin(String vin) {
			this.vin = vin;
		}
		public long getEventTime() {
			return eventTime;
		}
		public void setEventTime(long eventTime) {
			this.eventTime = eventTime;
		}
		public Zone getZone() {
			return zone;
		}
		public void setZone(Zone zone) {
			this.zone = zone;
		}
		@Override
		public int hashCode() {
			return Objects.hash(carId);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CarEvent other = (CarEvent) obj;
			return Objects.equals(carId, other.carId) && Objects.equals(vin, other.vin);
		}
		@Override
		public int compareTo(CarEvent o) {
			int result = carId.compareTo(o.getCarId());
			if (result == 0) {
				result = Long.compare(eventTime, o.getEventTime());
			}
			return result;
		}
		@Override
		public String toString() {
			return String.format("CarEvent [latitude=%s, longitude=%s, elevation=%s, carId=%s, eventTime=%s, zone=%s, vin=%s]",
					latitude, longitude, elevation, carId, eventTime, zone, vin);
		}
	}

	public static class Position {
		private double lat;
		private double lng;
		public double getLat() {
			return lat;
		}
		public void setLat(double lat) {
			this.lat = lat;
		}
		public double getLng() {
			return lng;
		}
		public void setLng(double lng) {
			this.lng = lng;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Metadata {
		private String name;
		private String resourceVersion;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getResourceVersion() {
			return resourceVersion;
		}
		public void setResourceVersion(String resourceVersion) {
			this.resourceVersion = resourceVersion;
		}
		@Override
		public int hashCode() {
			return Objects.hash(name, resourceVersion);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Metadata other = (Metadata) obj;
			return Objects.equals(name, other.name) && Objects.equals(resourceVersion, other.resourceVersion);
		}
		@Override
		public String toString() {
			return String.format("Metadata [name=%s, resourceVersion=%s]", name, resourceVersion);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Zone implements Comparable<Zone>{
		private Metadata metadata;
		private ZoneSpec spec;
		public Metadata getMetadata() {
			return metadata;
		}
		public void setMetadata(Metadata metaData) {
			this.metadata = metaData;
		}
		public ZoneSpec getSpec() {
			return spec;
		}
		public void setSpec(ZoneSpec spec) {
			this.spec = spec;
		}

		public boolean isInside(double longitude, double latitude) {
			return distanceTo(longitude, latitude) <= spec.getRadius();
		}

		private double distanceTo(double longitude, double latitude) {
			double lat1 = spec.getPosition().getLat();
			double lon1 = spec.getPosition().getLng();
			double lat2 = latitude;
			double lon2 = longitude;

			int R = 6371000; // metres
			double phi1 = lat1 * Math.PI/180; // φ, λ in radians
			double phi2 = lat2 * Math.PI/180;
			double deltaPhi = (lat2-lat1) * Math.PI/180;
			double deltaLambda = (lon2-lon1) * Math.PI/180;

			double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
					Math.cos(phi1) * Math.cos(phi2) *
							Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

			return R * c; // in metres

		}
		@Override
		public int compareTo(Zone o) {
			return Integer.compare(this.getSpec().getPriority(), o.getSpec().getPriority());
		}
		@Override
		public int hashCode() {
			return Objects.hash(metadata);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Zone other = (Zone) obj;
			return Objects.equals(metadata, other.metadata);
		}
		@Override
		public String toString() {
			return String.format("Zone [metadata=%s, spec=%s]", metadata, spec);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ZoneSpec {
		private String name;
		private Position position;
		private int priority;
		private int radius;
		private String type;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Position getPosition() {
			return position;
		}
		public void setPosition(Position position) {
			this.position = position;
		}
		public int getPriority() {
			return priority;
		}
		public void setPriority(int priority) {
			this.priority = priority;
		}
		public int getRadius() {
			return radius;
		}
		public void setRadius(int radius) {
			this.radius = radius;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		@Override
		public int hashCode() {
			return Objects.hash(name, position, priority, radius, type);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ZoneSpec other = (ZoneSpec) obj;
			return Objects.equals(name, other.name) && Objects.equals(position, other.position)
					&& priority == other.priority && radius == other.radius && Objects.equals(type, other.type);
		}
		@Override
		public String toString() {
			return String.format("ZoneSpec [name=%s, position=%s, priority=%s, radius=%s, type=%s]", name, position,
					priority, radius, type);
		}
	}

	boolean isDrogue() {
		return "drogue".equals(this.applicationMode);
	}

	@Override
	public void configure() throws Exception {
		Configuration cacheConfig = createCacheConfig();
		initRemoteCache(cacheConfig);
		bindToRegistry("cacheContainerConfiguration", cacheConfig);

		restConfiguration().host("0.0.0.0").port(9080).component("netty-http")
				.bindingMode(RestBindingMode.json)
				.dataFormatProperty("prettyPrint", "true")
				.contextPath("/");

		///mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		storeZonesInCacheRoute();
		storeCarEventsInCacheRoute();
		if (isDrogue()) {
			notifyZoneChangeEventRoute();
		}
		clearCacheEndpoint();

		if (aggregationInterval > 0) {
			storeAggregatedSnaphotOfCarEventsInCacheRouteJson();
		}
	}

	private void clearCacheEndpoint() {
		from("rest:get:clearCache").routeId("clearCacheEndpoint")
				.setHeader("Access-Control-Allow-Origin",constant("*"))
				.process(ex -> {
					zonesCache.clear();
					carsCache.clear();
					carsnapshotsCache.clear();
				})
				.log("Cleared all caches");
	}

	private void storeAggregatedSnaphotOfCarEventsInCacheRouteJson() {

		from("direct:aggregateSnapshotOfCarEventsInCache")
				.process(ex ->
						ex.getIn().setBody(mapper.readValue(ex.getIn().getBody(String.class), CarEvent.class))
				)
				.aggregate(simple("true"), new GroupedBodyAggregationStrategy())
				.completionInterval(aggregationInterval).id("myAggregator")
				.aggregateController(new DefaultAggregateController())
				.process(ex -> {
					@SuppressWarnings("unchecked")
					List<CarEvent> aggregation = ex.getIn().getBody(List.class);
					Stream<CarEvent> aggStream = aggregation.stream().sorted();
					if (aggregationDistinct) {
						aggStream = aggStream.distinct();
					}
					ex.getIn().setBody("[" + aggStream
							.map(c -> {
								try {
									return mapper.writeValueAsString(c);
								} catch (JsonProcessingException e) {
									LOGGER.error("Error writing json as string", e);
									return null;
								}
							}).collect(Collectors.joining(",")) + "]");
				})
				.log("Aggregated for the given interval: ${body}")
				.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
				.setHeader(InfinispanConstants.KEY).expression(simple("aggregated"))
				.setHeader(InfinispanConstants.VALUE).expression(simple("${body}"))
				.to("infinispan://{{com.redhat.bobbycar.camelk.dg.car.snapshot.cacheName}}?cacheContainerConfiguration=#cacheContainerConfiguration");

		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=kafkaToDatagridAggregatorCamelClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}").routeId("storeAggregatedSnaphotOfCarEventsInCacheRouteJson")
				//.unmarshal().json(JsonLibrary.Jackson, CarEvent.class)
				// FIXME: drogue
				.process(KafkaToDatagridRoute::processCloudEvent)
				.filter(simple("${header[ce_subject]} == 'car'"))
				.to("direct:aggregateSnapshotOfCarEventsInCache");

	}

	private void storeZonesInCacheRoute() throws Exception {
		// restConfiguration().component("netty-http").host("https://"+ocpAPIHost).port(6443).bindingMode(RestBindingMode.json);
		bindToRegistry("sslConfiguration", configureSslForApiAccess(ocpAPIHost));
		String token = retrieveServiceAccountToken();
		from("scheduler://foo?delay={{com.redhat.bobbycar.camelk.dg.refresh.interval}}").routeId("storeZonesInCache")
				.process(ex -> {
					zonesCache.clear();
				})
				.setHeader("Authorization").constant("Bearer " + token)
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
				.setHeader("Connection", constant("Keep-Alive"))
				.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.to("netty-http:https://" + ocpAPIHost + ":6443/apis/bobbycar.redhat.com/v1alpha1/namespaces/" + namespace + "/bobbycarzones?sslContextParameters=#sslConfiguration&keepAlive=true")
				//.log("Response was ${body}")
				.process(exchange -> {
					NettyHttpMessage msg = exchange.getIn(NettyHttpMessage.class);
					String payload = msg.getBody(String.class);
					String zonesData = payload.substring(payload.indexOf("\"items\":[")+8, payload.indexOf(",\"kind\":\"BobbycarZoneList\""));
					exchange.getIn().setBody(zonesData);
				})
				.split().jsonpathWriteAsString("$")
				//.log("Item is ${body} of type ${body.class}")
				.setHeader(InfinispanConstants.KEY).expression(jsonpath("$.metadata.name", String.class))
				.setHeader(InfinispanConstants.VALUE).expression(simple("${body}"))
				.log("Saving data to cache with value: ${headers[CamelInfinispanValue]}")
				.to("infinispan://{{com.redhat.bobbycar.camelk.dg.zone.cacheName}}?cacheContainerConfiguration=#cacheContainerConfiguration");
	}

	private String retrieveServiceAccountToken() throws IOException {
		Path filePath = Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/token");
		return new String(Files.readAllBytes(filePath));
	}

	/**
	 * Process Cloud Events.
	 * <p>
	 * This is a very basic processing of Cloud Events. It simply normalizes the events on the binary encoding. We need to do this since:
	 * a) Camel doesn't haven good support for Cloud Events so far and b) the sender may choose the format (binary, structured) and we need to
	 * handle either of them.
	 * <p>
	 * So this method will normalize on the binary encoding, so that functionality following this call can always expect the same format.
	 */
	private static void processCloudEvent(Exchange ex) throws Exception {
		var contentType = ex.getIn().getHeader("content-type", String.class);
		if (contentType != null && contentType.startsWith("application/cloudevents+json")) {
			// structured mode
			var value = Jsoner.deserialize(ex.getIn(String.class));
			if ( !(value instanceof JsonObject )) {
				return;
			}
			var json = (JsonObject)value;

			for (var entry : json.entrySet()) {
				switch (entry.getKey()) {
				case "datacontenttype":
					ex.getIn().setHeader("content-type", entry.getValue());
					break;
				case "data":
					ex.getIn().setBody(Jsoner.serialize(entry.getValue()));
					break;
				case "data_base64":
					if(entry.getValue() != null) {
						var data = Base64.getDecoder().decode(entry.getValue().toString());
						ex.getIn().setBody(data);
					}
					break;
				default:
					ex.getIn().setHeader("ce_" + entry.getKey(), entry.getValue());
					break;
				}

			}
		} else {
			// binary mode
			// nothing to do
		}
	}

	private void storeCarEventsInCacheRoute() {
		// clear the cars cache before starting the route
		carsCache.clear();

		from("direct:storeCarEvent")
				.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
				.setHeader(InfinispanConstants.KEY).expression(jsonpath("$.carid"))
				//.unmarshal().json(JsonLibrary.Jackson, CarEvent.class)
				.process(ex ->
						ex.getIn().setBody(mapper.readValue(ex.getIn().getBody(String.class), CarEvent.class))
				)
				.log("Received ${body} from  ${body.class}")
				.process(this::processZoneData)
				//.marshal().json(JsonLibrary.Jackson, String.class)
				.process(ex ->
						ex.getIn().setBody(mapper.writeValueAsString(ex.getIn().getBody(CarEvent.class)))
				)
				.setHeader(InfinispanConstants.VALUE).expression(simple("${body}"))
				.setHeader(InfinispanConstants.RESULT_HEADER).expression(simple("dummyAvoidOverwritingBody"))
				.log("Saving data to cache with key: ${headers[CamelInfinispanKey]} and value: ${body} of type  ${body.class}")
				.to("infinispan://{{com.redhat.bobbycar.camelk.dg.car.cacheName}}?cacheContainerConfiguration=#cacheContainerConfiguration")
				.choice()
				.when(header(ZONE_CHANGE_HEADER).isEqualTo(true))
				.process(this::transformToZoneChangeEvent)
				//.marshal().json(JsonLibrary.Jackson, String.class)
				.process(ex ->
						ex.getIn().setBody(mapper.writeValueAsString(ex.getIn().getBody(ZoneChangeEvent.class)))
				)
				.log("Publishing zone change ${body} to kafka")
				.to("direct:deliverZoneChange");

		var from = from("kafka:{{com.redhat.bobbycar.camelk.kafka.topic}}?clientId=kafkaToDatagridCamelClient&brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}").routeId("storeCarEventsInCache")
				.log(LoggingLevel.DEBUG, "Received ${body} from Kafka");

		if (isDrogue()) {
			from
					.process(KafkaToDatagridRoute::processCloudEvent)
					.log(LoggingLevel.DEBUG, "Post CE processing: ${body}")
					.filter(simple("${header[ce_subject]} == 'car'"))
					.to("direct:storeCarEvent")
					.end(); // ce_type == 'car'

			from("direct:deliverZoneChange")
					.log("Publishing zone change ${body} to kafka")
					.to("kafka:{{com.redhat.bobbycar.camelk.kafka.topicZoneChange}}?brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}");

		} else {
			from
					.to("direct:storeCarEvent");

			from("direct:deliverZoneChange")
					.log("Publishing ${body} to mqtt")
					.to("paho:{{com.redhat.bobbycar.camelk.mqtt.topic}}?brokerUrl={{com.redhat.bobbycar.camelk.mqtt.brokerUrl}}");
		}

	}

	@SuppressWarnings("unchecked")
	private void transformToZoneChangeEvent(Exchange ex) {
		Optional<Zone> previousZone = (Optional<Zone>) ex.getIn().getHeader(ZONE_PREV_HEADER);
		Optional<Zone> nextZone = (Optional<Zone>) ex.getIn().getHeader(ZONE_NXT_HEADER);
		String carId = (String) ex.getIn().getHeader(CAR_ID_HEADER);
		String vin = (String) ex.getIn().getHeader(VIN_HEADER);
		ex.getIn().setBody(new ZoneChangeEvent(previousZone.map(z -> z.getMetadata().getName()).orElse(null),
				nextZone.map(z -> z.getMetadata().getName()).orElse(null), carId, vin));
	}

	private void notifyZoneChangeEventRoute() throws Exception {
		URL url = new URL(this.drogueCommandEndpoint);
		bindToRegistry("sslConfiguration", configureSslForApiAccess(url.getHost()));
		var basicAuth = Base64.getEncoder().encodeToString(String.format("%s:%s", drogueCommandUser, drogueCommandToken).getBytes(StandardCharsets.UTF_8));
		log.info("Sending zone change event to device");
		from("kafka:{{com.redhat.bobbycar.camelk.kafka.topicZoneChange}}?brokers={{com.redhat.bobbycar.camelk.kafka.brokers}}&groupId=commandConsumer")
				.removeHeaders("*", "carid")
				.setHeader("Authorization").constant("Basic " + basicAuth)
				.setHeader(Exchange.HTTP_METHOD, constant("POST"))
				.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.toD("netty-http:" + drogueCommandEndpoint
						+ "/api/command/v1alpha1/apps/"
						+ this.drogueApplication
						+ "/devices/${header.carid}?command=zonechange&sslContextParameters=#sslConfiguration")
				.end();
	}

	private void processZoneData(Exchange ex) {
		CarEvent car = ex.getIn().getBody(CarEvent.class);
		double lat = car.getLatitude();
		double lng = car.getLongitude();
		ex.getIn().setHeader(CAR_ID_HEADER, car.getCarId());
		ex.getIn().setHeader(VIN_HEADER, car.getVin());
		Optional<Zone> matchingZone = zonesCache.values().stream()
				.map(zs -> {
					try {
						return mapper.readValue(zs, Zone.class);
					} catch (JsonProcessingException e) {
						LOGGER.error("Error marshalling zone", e);
						return null;
					}
				})
				.filter(z -> z.isInside(lng, lat))
				.sorted()
				.findFirst();
		Optional<Zone> previousZone = getPreviousZoneFromCache(car.carId);
		if (!previousZone.equals(matchingZone)) {
			LOGGER.error("Zone changed from {} to {}", previousZone, matchingZone);
			ex.getIn().setHeader(ZONE_CHANGE_HEADER, true);
			ex.getIn().setHeader(ZONE_PREV_HEADER, previousZone);
			ex.getIn().setHeader(ZONE_NXT_HEADER, matchingZone);
		}
		car.setZone(matchingZone.orElse(null));
	}

	private Optional<Zone> getPreviousZoneFromCache(String carId) {
		try {
			if (carsCache.containsKey(carId)) {
				CarEvent carEventFromCache = mapper.readValue(carsCache.get(carId), CarEvent.class);
				return Optional.ofNullable(carEventFromCache.getZone());
			}
			else {
				return Optional.empty();
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error marshalling carevent", e);
			return Optional.empty();
		}
	}

	private void initRemoteCache(Configuration cacheConfig) {
		cacheManager = new RemoteCacheManager(cacheConfig);
		cacheManager.start();
		zonesCache = cacheManager.administration().getOrCreateCache(zonesCacheName, CACHE_TEMPLATE);
		carsCache = cacheManager.administration().getOrCreateCache(carsCacheName, CACHE_TEMPLATE);
		carsnapshotsCache = cacheManager.administration().getOrCreateCache(carsnapshotCacheName, CACHE_TEMPLATE);
	}

	private Configuration createCacheConfig() {
		ConfigurationBuilder hotRodBuilder = new ConfigurationBuilder();
		return hotRodBuilder.addServer()
				.host(datagridHost).port(11222)
				.marshaller(new StringMarshaller(Charset.defaultCharset()))
				.clientIntelligence(ClientIntelligence.HASH_DISTRIBUTION_AWARE)
				.security()
				//.authentication().enable()
				//.username(datagridUsername)
				//.password(datagridPassword)
				//.serverName("infinispan")
				//.saslQop(SaslQop.AUTH)
				//.saslMechanism("DIGEST-MD5")
				.ssl()
				.sniHostName(datagridHost)
				.trustStoreFileName(PATH_TO_SERVICE_CA)
				.trustStoreType("pem")
				.build();
	}

	private SSLContextParameters configureSslForApiAccess(String host) {
		SSLContextParameters params = new SSLContextParameters();
		params.setCamelContext(getContext());
		SSLContextClientParameters clientParameters = new SSLContextClientParameters();
		clientParameters.setSniHostName(host);
		FilterParameters cipherSuitesFilter = new FilterParameters();
		cipherSuitesFilter.getInclude().add(".*");
		clientParameters.setCipherSuitesFilter(cipherSuitesFilter);
		params.setClientParameters(clientParameters);
		TrustManagersParameters trustManagers = new TrustManagersParameters();
		trustManagers.setTrustManager(new X509TrustManager() {

			private X509Certificate apiServerCert;
			{
				CertificateFactory fact;
				try {
					fact = CertificateFactory.getInstance("X.509");
					FileInputStream is = new FileInputStream (new File(PATH_TO_SERVICE_CA));
					apiServerCert = (X509Certificate) fact.generateCertificate(is);
				} catch (CertificateException | FileNotFoundException e) {
					LOGGER.error("Error loading certificate", e);
				}
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// Allow all certs
				return new X509Certificate[0];
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// Allow all certs
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// Allow all certs
			}
		});
		params.setTrustManagers(trustManagers);
		return params;
	}

	@Override
	public OnCompletionDefinition onCompletion() {
		cacheManager.close();
		return super.onCompletion();
	}
}

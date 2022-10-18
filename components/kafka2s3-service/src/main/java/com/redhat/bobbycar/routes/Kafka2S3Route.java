package com.redhat.bobbycar.routes;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.OnCompletionDefinition;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.apache.camel.util.json.JsonObject;
import org.apache.camel.util.json.Jsoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


public class Kafka2S3Route extends RouteBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Kafka2S3Route.class);
	
	private AmazonS3 s3Client;
	
	@PropertyInject("s3.custom.endpoint.enabled")
    private String s3_custom_endpoint_enabled;

	@PropertyInject("s3.custom.endpoint.url")
    private String s3_custom_endpoint_url;
	
	@PropertyInject("s3.accessKey")
    private String s3_accessKey;
	
	@PropertyInject("s3.secretKey")
    private String s3_secretKey;
	
	@PropertyInject("s3.message.aggregation.interval")
    private String s3_message_aggregation_interval;
	
	@PropertyInject("s3.region")
    private String s3_region;

	@Override
	public void configure() throws Exception {

		setupAWSClient();
		storeGpsInS3();

	}
	
	private void setupAWSClient() {
		
		if(Boolean.valueOf(s3_custom_endpoint_enabled)) {
			
			LOGGER.info("Custom S3 endpoint is enabled. Using: "+s3_custom_endpoint_url);
			
			System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
			
			s3Client = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTPS))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3_accessKey, s3_secretKey)))
                .withEndpointConfiguration(new EndpointConfiguration(s3_custom_endpoint_url, ""))
                .enablePathStyleAccess()
                .build();
			
		} else {
			
			LOGGER.info("Using AWS S3 endpoint");
			
			s3Client = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTPS))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3_accessKey, s3_secretKey)))
                .withRegion(s3_region)
                .enablePathStyleAccess()
                .build();
		}
		
		bindToRegistry("client", s3Client);

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
			if ( !(value instanceof JsonObject)) {
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

	private void storeGpsInS3() {
		from("kafka:{{kafka.broker.topic.gps}}?brokers={{kafka.broker.uri}}")
			.process(Kafka2S3Route::processCloudEvent)
			.filter(simple("${header[ce_subject]} == 'car'"))
				.convertBodyTo(String.class)
				.aggregate(simple("true"), new GroupedBodyAggregationStrategy()).completionInterval(Long.valueOf(s3_message_aggregation_interval))
				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						List<Exchange> data = exchange.getIn().getBody(List.class);
						StringBuffer sb = new StringBuffer();
						for (Iterator iterator = data.iterator(); iterator.hasNext();) {
							String ex = (String) iterator.next();
							sb.append(ex+"\n");
						}
						exchange.getIn().setBody(new ByteArrayInputStream(sb.toString().getBytes()));
					}
				})
				.setHeader(S3Constants.KEY, simple("bobbycar-gps-${date:now}.txt"))
				.to("aws-s3://{{s3.bucket.name}}?amazonS3Client=#client")
				.log("Uploaded Vibration dataset to S3")
			.end();
	}

	@Override
	public OnCompletionDefinition onCompletion() {
		return super.onCompletion();
	}
}

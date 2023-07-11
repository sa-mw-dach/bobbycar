package com.redhat.bobbycar;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.bobbycar.model.Aggregation;
import com.redhat.bobbycar.model.CarMetricsEvent;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.kafka.CloudEventDeserializer;
import io.cloudevents.kafka.CloudEventSerializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.streams.state.WindowStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AverageAggregator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AverageAggregator.class);

    static final String CAR_METRICS_STORE = "time-windowed-aggregated-stream-store";

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "com.redhat.bobbycar.streams.metrics.topic", defaultValue = "bobbycar-metrics")
    String metrics_topic;
    @ConfigProperty(name = "com.redhat.bobbycar.streams.metrics.aggregated.topic", defaultValue = "bobbycar-metrics-aggregated")
    String metrics_aggregated_topic;
    @ConfigProperty(name = "com.redhat.bobbycar.streams.speedalert.topic", defaultValue = "bobbycar-speed-alert")
    String speed_alert_topic;
    @ConfigProperty(name = "com.redhat.bobbycar.streams.speedalert.threshold", defaultValue = "100")
    double speed_alert_threshold;
    @ConfigProperty(name = "com.redhat.bobbycar.streams.metrics.windowsize.minutes", defaultValue = "2")
    int metrics_aggregation_windowsize;

    enum Mode {
        DIRECT,
        DROGUE,
    }

    @ConfigProperty(name = "com.redhat.bobbycar.streams.mode", defaultValue = "DIRECT")
    Mode mode;

    @Produces
    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();
        Serde<Aggregation> aggregationSerde = new ObjectMapperSerde<>(Aggregation.class);
        Serde<CarMetricsEvent> metricsSerde = new ObjectMapperSerde<>(CarMetricsEvent.class);

        Duration windowSize = Duration.ofMinutes(metrics_aggregation_windowsize);
        Duration advanceSize = Duration.ofSeconds(30);
        TimeWindows hoppingWindow = TimeWindows.of(windowSize).advanceBy(advanceSize);

        final KGroupedStream<String, CarMetricsEvent> stream;

        if (mode == Mode.DROGUE) {
            var eventsSerde = new Serdes.WrapperSerde<>(new CloudEventSerializer(), new CloudEventDeserializer());
            stream = builder.stream(metrics_topic, Consumed.with(Serdes.String(), eventsSerde))
                    .peek((key, value) -> {
                        var payload = Optional.ofNullable(value.getData()).map(CloudEventData::toBytes).map(String::new);
                        LOGGER.warn("Incoming record - key {} value {}", key, payload);
                    })
                    .flatMapValues(event -> {
                        if (!"carMetrics".equals(event.getSubject())) {
                            return Collections.<CarMetricsEvent>emptyList();
                        }
                        try {
                            return Collections.singleton(objectMapper.readerFor(CarMetricsEvent.class)
                                    .readValue(event.getData().toBytes()));
                        }
                        catch (Exception e ) {
                            LOGGER.info("Invalid event payload, skipping event: {e}");
                            return Collections.emptyList();
                        }
                    })
                    .selectKey((k,v) -> v.getVin())
                    .groupByKey(Grouped.with(Serdes.String(), metricsSerde));
        } else {
            stream = builder.stream(metrics_topic, Consumed.with(Serdes.String(), metricsSerde))
                    // .peek((key, value) -> System.out.println("Incoming record - key " + key +" value " + value))
                    .groupByKey();
        }

        stream
                .windowedBy(hoppingWindow)
                .aggregate(
                        Aggregation::new,
                        (vin, carMetricsEvent, aggregation) -> aggregation.updateFrom(carMetricsEvent),
                        Materialized.<String, Aggregation, WindowStore<Bytes, byte[]>>as(CAR_METRICS_STORE)
                                    .withKeySerde(Serdes.String())
                                    .withValueSerde(aggregationSerde)
                )
                //.suppress(untilWindowCloses(unbounded()))
                .toStream()
                .map((wk, value) -> KeyValue.pair(wk.key(),value))
                //.peek((key, value) -> System.out.println("Outgoing record - key " +key +" value " + value.toString()))
                .to(
                        metrics_aggregated_topic,
                        Produced.with(Serdes.String(), aggregationSerde)
                );

        builder.stream(metrics_aggregated_topic, Consumed.with(Serdes.String(), aggregationSerde))
                .filter((key,aggregate) -> (aggregate.speedAvg >= speed_alert_threshold))
                //.peek((key, value) -> System.out.println("Speed Alert for - VIN: " +key +" Speed: " + value.speedAvg))
                .to(
                        speed_alert_topic,
                        Produced.with(Serdes.String(), aggregationSerde)
                );

        return builder.build();
    }
}

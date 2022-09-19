package com.redhat.bobbycar;

import java.time.Duration;
import javax.enterprise.inject.Produces;
import com.redhat.bobbycar.model.Aggregation;
import com.redhat.bobbycar.model.CarMetricsEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.streams.state.WindowStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class AverageAggregator {

    static final String CAR_METRICS_STORE = "time-windowed-aggregated-stream-store";

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

    @Produces
    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();
        ObjectMapperSerde<Aggregation> aggregationSerde = new ObjectMapperSerde<>(Aggregation.class);
        ObjectMapperSerde<CarMetricsEvent> metricsSerde = new ObjectMapperSerde<>(CarMetricsEvent.class);

        Duration windowSize = Duration.ofMinutes(metrics_aggregation_windowsize);
        Duration advanceSize = Duration.ofSeconds(30);
        TimeWindows hoppingWindow = TimeWindows.of(windowSize).advanceBy(advanceSize);

        builder.stream(metrics_topic, Consumed.with(Serdes.String(), metricsSerde))
                //.peek((key, value) -> System.out.println("Incoming record - key " +key +" value " + value))
                .groupByKey()
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
                .filter((key,aggregate) -> aggregate.speedAvg > speed_alert_threshold)
                //.peek((key, value) -> System.out.println("Speed Alert for - VIN: " +key +" payload: " + value.toString()))
                .to(
                        speed_alert_topic,
                        Produced.with(Serdes.String(), aggregationSerde)
                );

        return builder.build();
    }
}

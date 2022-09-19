package com.redhat.bobbycar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.bobbycar.model.Aggregation;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.*;

import java.time.Instant;

@ApplicationScoped
public class InteractiveQueries {

    @Inject
    KafkaStreams streams;

    public Aggregation getCarMetricByVIN(String vin) {
        WindowStoreIterator<Aggregation> it = getCarMetricsStore().backwardFetch(vin, Instant.ofEpochMilli(0), Instant.now());
        if(it.hasNext()){
            System.out.println("Found WindowStore entry for id:" + vin);
            return it.next().value;
        }
        else {
            return null;
        }
    }

    private ReadOnlyWindowStore<String, Aggregation> getCarMetricsStore() {
        while (true) {
            try {
                return streams.store(StoreQueryParameters.fromNameAndType(AverageAggregator.CAR_METRICS_STORE, QueryableStoreTypes.windowStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}

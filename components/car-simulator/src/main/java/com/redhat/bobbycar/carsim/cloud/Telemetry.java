package com.redhat.bobbycar.carsim.cloud;

import com.redhat.bobbycar.carsim.cars.events.CarMetricsEvent;
import com.redhat.bobbycar.carsim.clients.model.KafkaCarPosition;
import com.redhat.bobbycar.carsim.drivers.Driver;

public interface Telemetry {
    void publishCarEvent(Driver driver, KafkaCarPosition carEvent);

    void publishCarMetricEvent(CarMetricsEvent evt);
}
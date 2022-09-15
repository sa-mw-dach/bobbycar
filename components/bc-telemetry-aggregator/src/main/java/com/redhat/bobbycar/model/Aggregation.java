package com.redhat.bobbycar.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Aggregation {

    public String vin;
    public int count = 0;
    public double fuelSum = 0;
    public double fuelAvg = 0;
    public double speedSum = 0;
    public double speedAvg = 0;
    public double rpmSum = 0;
    public double rpmAvg = 0;
    public double co2Sum = 0;
    public double co2Avg = 0;

    public Aggregation updateFrom(CarMetricsEvent metric) {

        vin = metric.getVin();
        count++;

        fuelSum += metric.getEngineData().getFuelConsumptionPer100km();
        fuelAvg = BigDecimal.valueOf(fuelSum / count).setScale(2, RoundingMode.HALF_UP).doubleValue();

        speedSum += metric.getEngineData().getSpeedInKmh();
        speedAvg = BigDecimal.valueOf(speedSum / count).setScale(2, RoundingMode.HALF_UP).doubleValue();

        rpmSum += metric.getEngineData().getRpm();
        rpmAvg = BigDecimal.valueOf(rpmSum / count).setScale(2, RoundingMode.HALF_UP).doubleValue();

        co2Sum += metric.getEngineData().getCo2Emission();
        co2Avg = BigDecimal.valueOf(co2Sum / count).setScale(2, RoundingMode.HALF_UP).doubleValue();

        return this;
    }
}

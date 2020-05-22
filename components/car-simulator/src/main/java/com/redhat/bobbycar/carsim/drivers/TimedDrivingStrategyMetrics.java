package com.redhat.bobbycar.carsim.drivers;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;

@ApplicationScoped
public class TimedDrivingStrategyMetrics {

	private long skewMillis;
	
	
	@Gauge(absolute = true, name = "com.redhat.bobbycar.carsim.skew", unit = MetricUnits.MILLISECONDS, description = "Should be negative otherwise processing power is not sufficient to simulate cars")
	public long getSkewMillis() {
		return skewMillis;
	}

	public void setSkewMillis(long skewMillis) {
		this.skewMillis = skewMillis;
	}

}

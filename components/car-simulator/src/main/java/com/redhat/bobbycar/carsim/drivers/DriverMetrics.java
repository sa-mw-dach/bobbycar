package com.redhat.bobbycar.carsim.drivers;

import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;

@ApplicationScoped
public class DriverMetrics {

	private AtomicLong pointsVisited = new AtomicLong(0);
	private AtomicLong carsDriving = new AtomicLong(0);

	@Gauge(absolute = true, name = "com.redhat.bobbycar.carsim.pointsvisited", unit = MetricUnits.NONE, description = "How many route points have been visited")
	public long getPointsVisited() {
		return pointsVisited.get();
	}

	public void incrementPointsVisited() {
		this.pointsVisited.incrementAndGet();
	}
	
	@Gauge(absolute = true, name = "com.redhat.bobbycar.carsim.carsdriving", unit = MetricUnits.NONE, description = "How many cars are currently driving")
	public long getCarsDriving() {
		return carsDriving.get();
	}

	public void incrementCarsDriving() {
		this.carsDriving.incrementAndGet();
	}
	
	public void decrementCarsDriving() {
		this.carsDriving.decrementAndGet();
	}

	
	
}

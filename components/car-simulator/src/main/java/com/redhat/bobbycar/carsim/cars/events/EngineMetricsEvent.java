package com.redhat.bobbycar.carsim.cars.events;

import java.util.Objects;

import com.redhat.bobbycar.carsim.cars.EngineData;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class EngineMetricsEvent {
	
	private EngineData engineData;
	
	public EngineMetricsEvent() {
		
	}
	
	public EngineMetricsEvent(EngineData engineData) {
		super();
		this.engineData = engineData;
	}
	
	public EngineData getEngineData() {
		return engineData;
	}

	public void setEngineData(EngineData engineData) {
		this.engineData = engineData;
	}

	@Override
	public int hashCode() {
		return Objects.hash(engineData);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EngineMetricsEvent other = (EngineMetricsEvent) obj;
		return Objects.equals(engineData, other.engineData);
	}


	@Override
	public String toString() {
		return String.format("EngineMetricsEvent [engineData=%s]", engineData);
	}

}

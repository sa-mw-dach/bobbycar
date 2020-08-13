package com.redhat.bobbycar.carsim.consumer;

import com.redhat.bobbycar.carsim.consumer.model.ZoneChangeEvent;

public interface ZoneChangeListener {
	
	public void onZoneChange(ZoneChangeEvent event);
	
}

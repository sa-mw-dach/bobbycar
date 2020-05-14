package com.redhat.bobbycar.carsim.clients.model;

import java.util.ArrayList;
import java.util.List;

public class KafkaCarEvent {
	
	private List<KafkaCarRecord> records = new ArrayList<>();

	public KafkaCarEvent() {
		
	}
	
	public KafkaCarEvent(List<KafkaCarRecord> records) {
		super();
		this.records = records;
	}

	public List<KafkaCarRecord> getRecords() {
		return records;
	}

	public void setRecords(List<KafkaCarRecord> records) {
		this.records = records;
	}
	
	
}

package com.redhat.bobbycar.carsim.clients.model;

import java.time.ZonedDateTime;

public class KafkaCarRecord {
	private String key;
	private KafkaCarPosition value;
	private long eventTime;
	
	public KafkaCarRecord() {
		super();
	}
	public KafkaCarRecord(String key, KafkaCarPosition value, ZonedDateTime time) {
		super();
		this.key = key;
		this.value = value;
		this.eventTime = time.toInstant().toEpochMilli();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public KafkaCarPosition getValue() {
		return value;
	}
	public void setValue(KafkaCarPosition value) {
		this.value = value;
	}
	/*TODO public long getEventTime() {
		return eventTime;
	}
	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}*/
}

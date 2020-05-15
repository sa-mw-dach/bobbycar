package com.redhat.bobbycar.carsim.clients.model;

public class KafkaCarRecord {
	private String key;
	private KafkaCarPosition value;
	
	
	public KafkaCarRecord() {
		super();
	}
	public KafkaCarRecord(String key, KafkaCarPosition value) {
		super();
		this.key = key;
		this.value = value;
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

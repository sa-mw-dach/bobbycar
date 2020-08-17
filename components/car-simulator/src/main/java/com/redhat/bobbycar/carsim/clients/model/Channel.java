package com.redhat.bobbycar.carsim.clients.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Channel {
	
	private String title;
	private List<Item> items = new ArrayList<>();
	
	public Channel() {
		super();
	}

	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@XmlElement(name = "item")
	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return String.format("Channel [title=%s, items=%s]", title, items);
	}
	
	
}

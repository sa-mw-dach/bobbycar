package com.redhat.bobbycar.carsim.clients.model;

import javax.xml.bind.annotation.XmlElement;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Item {
	private String link;
	
	@XmlElement(name = "link")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return String.format("Item [link=%s]", link);
	}
}

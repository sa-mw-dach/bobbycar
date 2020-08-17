package com.redhat.bobbycar.carsim.clients.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@XmlRootElement
public class Rss {
	
	private List<Channel> channelList = new ArrayList<>();
	@XmlElement(name = "channel")
	public List<Channel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<Channel> channels) {
		this.channelList = channels;
	}

	@Override
	public String toString() {
		return String.format("Rss [channels=%s]", channelList);
	}
	
}

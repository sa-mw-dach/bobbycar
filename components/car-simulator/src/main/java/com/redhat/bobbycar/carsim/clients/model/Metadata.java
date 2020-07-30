package com.redhat.bobbycar.carsim.clients.model;

import java.util.Objects;

public class Metadata {
	private String name;
	private String resourceVersion;

	public Metadata() {
		super();
	}

	public Metadata(String name, String resourceVersion) {
		super();
		this.name = name;
		this.resourceVersion = resourceVersion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceVersion() {
		return resourceVersion;
	}

	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, resourceVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Metadata other = (Metadata) obj;
		return Objects.equals(name, other.name) && Objects.equals(resourceVersion, other.resourceVersion);
	}

	@Override
	public String toString() {
		return String.format("Metadata [name=%s, resourceVersion=%s]", name, resourceVersion);
	}
}

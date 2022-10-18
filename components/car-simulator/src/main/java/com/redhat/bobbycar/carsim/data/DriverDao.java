package com.redhat.bobbycar.carsim.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.bobbycar.carsim.drivers.Driver;

@ApplicationScoped
public class DriverDao {
	private final Map<String, Driver> drivers = new HashMap<>();
	
	public Collection<Driver> getAllDrivers() {
		return drivers.values();
	}
	
	public Driver create(String id, Driver driver) {
		return drivers.put(id, driver);
	}
	
	public Optional<Driver> getById(String id) {
		return Optional.ofNullable(drivers.get(id));
	}
}

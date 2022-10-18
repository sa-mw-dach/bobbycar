package com.redhat.bobbycar.carsim.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.bobbycar.carsim.cars.Car;
import com.redhat.bobbycar.carsim.drivers.Driver;

@ApplicationScoped
public class CarDao {
    private final Map<String, Car> cars = new HashMap<>();

    public Collection<Car> getAllCars() {
        return cars.values();
    }

    public Car create(String id, Car car) {
        return cars.put(id, car);
    }

    public Optional<Car> getById(String id) {
        return Optional.ofNullable(cars.get(id));
    }
}

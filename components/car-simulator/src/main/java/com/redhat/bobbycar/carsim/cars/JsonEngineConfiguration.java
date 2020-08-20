package com.redhat.bobbycar.carsim.cars;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.cars.model.EngineBehavior;
import com.redhat.bobbycar.carsim.cars.model.GearBehavior;
import com.redhat.bobbycar.carsim.cars.model.SpeedPerRpm;


public class JsonEngineConfiguration implements EngineConfiguration {
	private static final String DEFAULT_ENGINE_CONFIG_JSON = "/engines/bmw_m3_coupe.json";
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonEngineConfiguration.class);
	private Jsonb jsonb;
	private EngineBehavior engineBehavior;
	private double rpmToSwitchGear = 4000;
	private int maxGear;
	private double consumptionIncreaseSpeedKmH = 40;
	private double minFuelConsumptionPer100km = 3;
	private double maxFuelConsumptionPer100km = 15;
	
	
	private static class GearEntry {
		private final int gear;
		private final SpeedPerRpm speedPerRpm;
		
		public GearEntry(int gear, SpeedPerRpm speedPerRpm) {
			super();
			this.gear = gear;
			this.speedPerRpm = speedPerRpm;
		}

		public int getGear() {
			return gear;
		}

		public double getRpm() {
			return speedPerRpm.getRpm();
		}
		
		public double getSpeed() {
			return speedPerRpm.getSpeed();
		}
		
		public double rpmAtSpeed(double speed) {
			return (speedPerRpm.getRpm() / speedPerRpm.getSpeed() * speed);
		}

		@Override
		public int hashCode() {
			return Objects.hash(gear, speedPerRpm);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GearEntry other = (GearEntry) obj;
			return gear == other.gear && Objects.equals(speedPerRpm, other.speedPerRpm);
		}

		@Override
		public String toString() {
			return String.format("GearEntry [gear=%s, speedPerRpm=%s]", gear, speedPerRpm);
		}
		
	}
	
	public JsonEngineConfiguration() throws FileNotFoundException {
		this.jsonb = JsonbBuilder.create();
		this.engineBehavior = readConfiguration();
		this.maxGear = this.engineBehavior.getGearBehaviors().stream()
				.max(Comparator.comparing(GearBehavior::getGear))
				.map(GearBehavior::getGear)
				.orElseThrow(() -> new EngineException("No gears found"));
	}
	
	private EngineBehavior readConfiguration() {
		return jsonb.fromJson(JsonEngineConfiguration.class.getResourceAsStream(DEFAULT_ENGINE_CONFIG_JSON),
				EngineBehavior.class);
	}
	
	@Override
	public Optional<Double> maxSpeed() {
		return engineBehavior.getGearBehaviors().stream()
			.flatMap(b -> b.getSpeedPerRpms().stream())
			.max(Comparator.comparing(SpeedPerRpm::getSpeed))
			.map(SpeedPerRpm::getSpeed);
	}
	
	
	@Override
	public Optional<Double> co2FromSpeed(double speed) {
		//TODO Implement this
		return Optional.of(120.0);
	}
	
	@Override
	public Optional<Double> fuelConsumptionPer100KmFromSpeed(double speed) {
		if (speed >= consumptionIncreaseSpeedKmH) {
			return maxSpeed()
					.flatMap(max -> calculateFuelConsumptionByKmH(speed, max));
		} 
		else {
			return Optional.of(minFuelConsumptionPer100km);
		}
	}

	private Optional<? extends Double> calculateFuelConsumptionByKmH(double speed, Double max) {
		LOGGER.debug("Max speed is {}", max);
		if (speed > max) {
			return Optional.empty();
		} else {
			double speedRange = max - consumptionIncreaseSpeedKmH;
			double consumptionRange = maxFuelConsumptionPer100km - minFuelConsumptionPer100km;
			return Optional.of(consumptionRange / speedRange * (speed - consumptionIncreaseSpeedKmH) + minFuelConsumptionPer100km);
		}
	}
	
	@Override
	public Optional<Double> rpmFromSpeed(double speed) {
		return gearEntryFromSpeed(speed)
				.map(g -> g.rpmAtSpeed(speed));
	}

	@Override
	public Optional<Integer> gearFromSpeed(double speed) {
		LOGGER.debug("Matching {} gear behaviors ", engineBehavior.getGearBehaviors().size());
		return gearEntryFromSpeed(speed)
				.map(GearEntry::getGear);
	}

	private Optional<GearEntry> gearEntryFromSpeed(double speed) {
		return engineBehavior.getGearBehaviors().stream()
			.sorted(Comparator.comparing(GearBehavior::getGear))
			.flatMap(b -> b.getSpeedPerRpms().stream().map(s -> new GearEntry(b.getGear(), s)))
				.filter(gearAndRpmCanAchieve(speed))
				.findFirst();
	}
	
	private Predicate<? super GearEntry> gearAndRpmCanAchieve(double speed) {
		return s -> (s.getRpm() <= rpmToSwitchGear && s.getSpeed() >= speed) 
				|| (s.getGear() == maxGear && s.getSpeed() >= speed);
	}
}

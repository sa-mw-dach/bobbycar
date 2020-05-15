package com.redhat.bobbycar.carsim;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.gpx.GpxReader;

public class DriverTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DriverTest.class);

	@ParameterizedTest
	@MethodSource
	public void driveRoute(Route route) throws InterruptedException {
		Driver driver = new Driver(route, new TimedDrivingStrategy());
		driver.registerCarEventListener(evt -> {
			LOGGER.error("Event is {}", evt);
			assertNotNull(evt);
		});
		Thread t = new Thread(driver);
		t.start();
		t.join();
	}

	private static Stream<Route> driveRoute() {
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime nowPlus5 = now.plusSeconds(5);
		return Stream.of(new Route("SinglePointTimed", new RoutePoint(new BigDecimal("8.89322222"), new BigDecimal("46.57652778"), new BigDecimal("100"), now)),
				new Route("DualPointTimed", new RoutePoint(new BigDecimal("8.89322222"), new BigDecimal("46.57652778"), new BigDecimal("100"), now),
						new RoutePoint(new BigDecimal("8.89344444"), new BigDecimal("46.57661111"), new BigDecimal("100"), nowPlus5)));
	}
	
	@ParameterizedTest
	@MethodSource
	public void driveGpxRoute(Route route) throws InterruptedException {
		Driver driver = new Driver(route, new TimedDrivingStrategy(100));
		driver.registerCarEventListener(evt -> {
			LOGGER.error("Event is {}", evt);
			assertNotNull(evt);
		});
		Thread t = new Thread(driver);
		t.start();
		t.join();
	}
	
	private static Stream<Route> driveGpxRoute() throws JAXBException {
		GpxReader reader = new GpxReader();
		String path = "src/test/resources/gps/gpx";
		File gpxDir = new File(path);
		
		File[] gpxFiles = gpxDir.listFiles(f -> "gpx".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		return Arrays.stream(gpxFiles).map(t -> {
			try {
				return reader.readGpx(t);
			} catch (JAXBException | IOException e) {
				e.printStackTrace();
				return null;
			}
		}).filter(gpx -> gpx != null);
	}
}

package com.redhat.bobbycar.carsim.gpx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;

import com.redhat.bobbycar.carsim.gpx.GpxReader.GpxVersion;
import com.redhat.bobbycar.carsim.routes.Route;

class GpxReadTest {

	@Test
	 void readExampleGpx() throws FileNotFoundException, JAXBException, IOException {
		String path = "src/test/resources/gps/gpx/example.gpx";
		GpxReader reader = new GpxReader();
		Route route = reader.readGpx(new File(path));
		assertNotNull(route);
		assertEquals(7, route.getPoints().count());
	}
	
	@Test
	 void readExampleGpx10() throws FileNotFoundException, JAXBException, IOException {
		String path = "src/test/resources/gps/gpx/example_10.gpx";
		GpxReader reader = new GpxReader();
		Route route = reader.readGpx(new File(path), GpxVersion.V10);
		assertNotNull(route);
		assertEquals(1, route.getPoints().count());
	}
	
	@Test
	 void readExample2Gpx() throws FileNotFoundException, JAXBException, IOException {
		String path = "src/test/resources/gps/gpx/example2.gpx";
		GpxReader reader = new GpxReader();
		Route route = reader.readGpx(new File(path));
		assertNotNull(route);
		assertEquals(1, route.getPoints().count());
	}
	
	@Test
	 void readFullExample10Gpx() throws FileNotFoundException, JAXBException, IOException {
		String path = "src/test/resources/gps/gpx/example_full_10.gpx";
		GpxReader reader = new GpxReader();
		Route route = reader.readGpx(new File(path), GpxVersion.V10);
		assertNotNull(route);
		assertEquals(1, route.getPoints().count());
	}
	
	@Test
	 void readFullExampleGpx() throws FileNotFoundException, JAXBException, IOException {
		String path = "src/test/resources/gps/gpx/example_full_11.gpx";
		GpxReader reader = new GpxReader();
		Route route = reader.readGpx(new File(path));
		assertNotNull(route);
		assertEquals(1, route.getPoints().count());
	}
}

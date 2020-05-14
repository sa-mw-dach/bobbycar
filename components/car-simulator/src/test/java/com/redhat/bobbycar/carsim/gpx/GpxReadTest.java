package com.redhat.bobbycar.carsim.gpx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;

public class GpxReadTest {

	@Test
	public void readGpx() throws FileNotFoundException, JAXBException, IOException {
		String path = "src/test/resources/gps/gpx/example.gpx";
		GpxReader reader = new GpxReader();
		reader.readGpx(new File(path));
		
	}
}

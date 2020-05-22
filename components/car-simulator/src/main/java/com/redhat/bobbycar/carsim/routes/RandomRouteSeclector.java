package com.redhat.bobbycar.carsim.routes;

import java.io.File;
import java.util.Random;

import com.redhat.bobbycar.carsim.gpx.GpxReader;

public class RandomRouteSeclector extends FileBasedRouteSelector{
	
	private Random random = new Random();
	
	public RandomRouteSeclector(GpxReader gpxReader, String pathToRoutes) {
		super(gpxReader, pathToRoutes);
	}
	
	@Override
	File nextRoute() {
		return gpxFiles[random.nextInt(gpxFiles.length)];
	}

}

package com.redhat.bobbycar.carsim.routes;

import java.io.IOException;

import javax.xml.bind.JAXBException;

public interface RouteSelectionStrategy {

	Route selectRoute() throws JAXBException, IOException;

}

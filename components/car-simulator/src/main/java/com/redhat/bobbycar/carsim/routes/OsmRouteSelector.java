package com.redhat.bobbycar.carsim.routes;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.clients.OsmRoutesService;
import com.redhat.bobbycar.carsim.clients.model.Channel;
import com.redhat.bobbycar.carsim.clients.model.Item;
import com.redhat.bobbycar.carsim.gpx.GpxReader;
import com.redhat.bobbycar.carsim.gpx.GpxType;
import com.redhat.bobbycar.carsim.gpx10.Gpx;

@ApplicationScoped
public class OsmRouteSelector implements RouteSelectionStrategy{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OsmRouteSelector.class);
	
	@Inject
	@RestClient
	OsmRoutesService routesService;
	
	@Inject
	GpxReader gpxReader;
	
	Channel gpxChannel;
	private Unmarshaller unmarshaller;
	private Random random = new Random();
	
	public OsmRouteSelector() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, Gpx.class);
		unmarshaller = jaxbContext.createUnmarshaller();
	}
	
	@Override
	public Route selectRoute() {
		initChannel();
		selectRoute(random.nextInt(routes()));
		return null;
	}
	
	private Route fromItem(Item item) {
		String[] splitLink = item.getLink().split("/");
		String traceId = splitLink[splitLink.length - 1];
		LOGGER.debug("Loading trace data with trace id {} ", traceId);
		
		return getTraceData(traceId);
	}
	
	private Route getTraceData(String traceId) {
		String rawTraceData = routesService.getRawTraceData(traceId);
		try {
			Object gpxObj = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(rawTraceData.getBytes())));
			if (gpxObj instanceof JAXBElement<?>) {
				JAXBElement<?> gpx = (JAXBElement<?>) gpxObj;
				if (gpx.getValue() instanceof Gpx) {
					return gpxReader.transform10((Gpx) gpx.getValue());
				} else if (gpx.getValue() instanceof GpxType) {
					return gpxReader.transform((GpxType) gpx.getValue());
				} else {
					throw new RouteSelectionException("Cannot determine gpx version from unmarshalled type " + gpx.getValue().getClass());
				}
			} else if (gpxObj instanceof Gpx) {
				return gpxReader.transform10((Gpx) gpxObj);
			} else if (gpxObj instanceof GpxType) {
				return gpxReader.transform((GpxType) gpxObj);
			} else {
				throw new RouteSelectionException("Cannot determine gpx version from unmarshalled type " + gpxObj.getClass());
			}
			
		} catch (JAXBException e) {
			throw new RouteSelectionException("Cannot unmarshal gpx", e);
		}
	}

	@Override
	public Route selectRoute(int route) {
		initChannel();
		return fromItem(gpxChannel.getItems().get(route));
	}

	@Override
	public int routes() {
		initChannel();
		return gpxChannel.getItems().size();
	}
	
	public void initChannel() {
		if (gpxChannel == null) {
			LOGGER.debug("Initializing channel list from osm");
			List<Channel> channelList = routesService.getTraces().getChannelList();
			if (!channelList.isEmpty()) {
				this.gpxChannel = channelList.get(0);
			}
		} else {
			LOGGER.debug("Channel list already initialized");
		}
	}

}

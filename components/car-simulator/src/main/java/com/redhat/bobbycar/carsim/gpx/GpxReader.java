package com.redhat.bobbycar.carsim.gpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.gpx10.Gpx;
import com.redhat.bobbycar.carsim.gpx10.Gpx.Trk;
import com.redhat.bobbycar.carsim.routes.Route;
import com.redhat.bobbycar.carsim.routes.RoutePoint;



@ApplicationScoped
public class GpxReader {
	private Unmarshaller unmarshaller;
	private static final Logger LOGGER = LoggerFactory.getLogger(GpxReader.class);
	
	enum GpxVersion {V11, V10}

	public GpxReader() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, Gpx.class);
		unmarshaller = jaxbContext.createUnmarshaller();
	}

	public Route readGpx(File input) throws JAXBException, IOException {
		return readGpx(input, GpxVersion.V11);
	}
	
	public Route readGpx(File input, GpxVersion version) throws JAXBException, IOException {
		try (FileInputStream fis = new FileInputStream(input)) {
			return readGpx(fis, version);
		}
	}

	public Route readGpx(InputStream input) throws JAXBException {
		return readGpx(input, GpxVersion.V11);

	}
	
	public Route readGpx(InputStream input, GpxVersion version) throws JAXBException {
		if (version == GpxVersion.V11) {
			Source source = new StreamSource(input);
			JAXBElement<GpxType> element = unmarshaller.unmarshal(source, GpxType.class);
			return transform(element.getValue());
		} else {
			Source source = new StreamSource(input);
			JAXBElement<Gpx> element = unmarshaller.unmarshal(source, Gpx.class);
			return transform10(element.getValue());
		}
	}

	public Route transform(GpxType gpx) {
		LOGGER.info("Transforming gpx into route: {}", gpx.getTrk());
		return new Route(gpx.getMetadata() != null ? gpx.getMetadata().getName() : null, transform(gpx.getTrk()));
	}
	
	public Route transform10(Gpx gpx) {
		LOGGER.info("Transforming gpx into route: {}", gpx.getTrk());
		return new Route(gpx.getName() != null ? gpx.getName() : null, transform10(gpx.getTrk()));
	}

	private List<RoutePoint> transform10(List<Trk> trks) {
		LOGGER.info("Transforming trks into route points: {}", trks);
		return trks.stream().flatMap(trk -> trk.getTrkseg().stream().flatMap(seg -> seg.getTrkpt().stream()))
				.map(trk -> new RoutePoint(trk.getLon(), trk.getLat(), trk.getEle(),
						trk.getTime() != null ? trk.getTime().toGregorianCalendar().toZonedDateTime() : null))
				.collect(Collectors.toList());

	}

	private List<RoutePoint> transform(List<TrkType> trks) {
		LOGGER.info("Transforming trks into route points: {}", trks);
		return trks.stream().flatMap(trk -> trk.getTrkseg().stream().flatMap(seg -> seg.getTrkpt().stream()))
				.map(trk -> new RoutePoint(trk.getLon(), trk.getLat(), trk.getEle(),
						trk.getTime() != null ? trk.getTime().toGregorianCalendar().toZonedDateTime() : null))
				.collect(Collectors.toList());

	}

}

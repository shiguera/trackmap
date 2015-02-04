package com.mlab.map.layer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.mlab.gpx.api.GpxDocument;
import com.mlab.gpx.api.GpxFactory;
import com.mlab.gpx.api.WayPoint;
import com.mlab.gpx.impl.Track;
import com.mlab.gpx.impl.TrackSegment;
import com.mlab.map.factory.GeoToolsMapFactory;
import com.mlab.map.factory.StyleFac;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Crea una capa vectorial a partir de un fichero GPX. Utiliza como
 * geometría el primer segmento del primer track del gpx. Ignora el
 * resto del contenido del fichero Gpx
 * @author shiguera
 *
 */
public class GpxLayer extends AbstractVectorLayer {
	private final Logger LOG = Logger.getLogger(getClass().getName());
	private final Color DEFAULT_WAYPOINT_COLOR = Color.RED;
	private final int DEFAULT_WAYPOINT_SIZE = 10;
	private final Color DEFAULT_TRACKLINE_COLOR = Color.RED;
	private final int DEFAULT_TRACKLINE_WIDTH = 4;

	final Color DEFAULT_COLOR = Color.RED;
	final int DEFAULT_WIDTH = 2;
	List<Layer> trackLayers;
	List<Layer> routeLayers;
	Layer wayPointLayer ;
	GpxFactory gpxFactory;
	GpxDocument gpxDocument;
	Style wayPointStyle, trackStyle;
	
	public GpxLayer() {
		//LOG.info("GpxLayer.GpxLayer()");	
		this.trackLayers = new ArrayList<Layer>();
		this.routeLayers = new ArrayList<Layer>();
		this.wayPointLayer = null;
		this.gpxDocument = null;
		this.wayPointStyle = createDeafultWayPointStyle();
		this.trackStyle = createDeafultTrackStyle();
	}
	
	public GpxLayer(GpxDocument gpxdoc) {
		this();		
		gpxFactory = GpxFactory.getFactory(GpxFactory.Type.ExtendedGpxFactory);
		setNameAndUrl(gpxdoc);

		gpxDocument = gpxdoc;
		if(gpxDocument == null) {
			LOG.error("GpxLayer.GpxLayer() ERROR: gpxDocument is null");
			return;
		} 
		
		
		createWayPointLayer();
		createRouteLayers();
		createTrackLayers();

	}
	private void setNameAndUrl(GpxDocument gpxdoc) {
		//LOG.info("GpxLayer.setNameAndUrl()");
		if(gpxdoc.getGpxFile()!=null) {
			this.url = gpxdoc.getGpxFile().getPath();
			this.name = gpxdoc.getGpxFile().getName();
		} else {
			this.url = "";
			this.name = "";
		}
	}
	private Style createDeafultWayPointStyle() {
		return StyleFac.createPointStyle(DEFAULT_WAYPOINT_COLOR, DEFAULT_WAYPOINT_SIZE);
	}
	private Style createDeafultTrackStyle() {
		return StyleFac.createLineStyle(DEFAULT_TRACKLINE_COLOR, DEFAULT_TRACKLINE_WIDTH);
	}
	private void createWayPointLayer() {
		//LOG.info("GpxLayer.createWayPointLayer()");	
		if(this.gpxDocument.hasWayPoints()) {
			SimpleFeatureType WPOINT = this.createWayPointFeatureType();
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(WPOINT);
			DefaultFeatureCollection collection = new DefaultFeatureCollection();		
			for(int i=0; i<gpxDocument.wayPointCount(); i++) {
				WayPoint wp = gpxDocument.getWayPoint(i);
				Coordinate c = new Coordinate(wp.getLongitude(), wp.getLatitude());
				Point p = GeoToolsMapFactory.getGeometryFactory().createPoint(c);
				featureBuilder.add(p);
				featureBuilder.add(wp.getName());
				featureBuilder.add(wp.getAltitude());
				SimpleFeature feature = featureBuilder.buildFeature(null);
				collection.add(feature);
			}
			if(collection.size()>0) {
				this.wayPointLayer = new FeatureLayer(collection, wayPointStyle);
			}
		}
	}
	private void createRouteLayers() {
		
	}
	private void createTrackLayers() {
		//LOG.info("GpxLayer.createTrackLayers()");
		if(gpxDocument.hasTracks()) {
			//LOG.info("GpxLayer.createTrackLayers(): gpxDocument has "+String.format("%d tracks", gpxDocument.trackCount()));	
			SimpleFeatureType featureType = createTrackFeatureType();
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
			DefaultFeatureCollection collection = null;
			for(int i=0; i<gpxDocument.trackCount(); i++) {
				//LOG.info("GpxLayer.createTrackLayers(): creating layer for track "+String.format("%d", i));
				Track track = gpxDocument.getTrack(i);
				collection = new DefaultFeatureCollection();		
				if(track.hasSegments()) {
					for(int j=0; j<track.segmentsCount(); j++) {
						//LOG.info("GpxLayer.createTrackLayers(): creating feature for segment "+String.format("%d", j));
						TrackSegment segment = track.getTrackSegment(j);
						//LOG.info("GpxLayer.createTrackLayers(): segment has "+String.format("%d points", segment.size()));
						LineString l = GeoToolsMapFactory.segmentToLinestringLonLat(segment);
						featureBuilder.add(l);
						featureBuilder.add(track.getName());
						SimpleFeature feature = featureBuilder.buildFeature(null);
						collection.add(feature);
					}
				}
				if(collection.size()>0) {
					Layer tlayer = new FeatureLayer(collection, trackStyle);
					this.trackLayers.add(tlayer);
				}				
			}
			//LOG.info("GpxLayer.createTrackLayers(): " +String.format("%d", trackLayers.size())+" layers created");
		}
	}
	
	private SimpleFeatureType createWayPointFeatureType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("WayPoint");
        builder.setCRS(GeoToolsMapFactory.getWGS84CRS()); // <- Coordinate reference system

        // add attributes in order
        builder.add("geom", Point.class);
        builder.length(20).add("Name",String.class);
        builder.add("Altitude",Double.class);
        // build the type
        final SimpleFeatureType WPOINT = builder.buildFeatureType();

        return WPOINT;
	}
	private SimpleFeatureType createTrackFeatureType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Track");
        builder.setCRS(GeoToolsMapFactory.getWGS84CRS()); // <- Coordinate reference system

        // add attributes in order
        builder.add("geom", LineString.class);
        builder.length(20).add("Name",String.class);
        // build the type
        final SimpleFeatureType TRACK = builder.buildFeatureType();

        return TRACK;
	}
	private SimpleFeatureType createRouteFeatureType() {
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Route");
        builder.setCRS(GeoToolsMapFactory.getWGS84CRS()); // <- Coordinate reference system

        // add attributes in order
        builder.add("geom", LineString.class);
        builder.length(20).add("Name",String.class);
        // build the type
        final SimpleFeatureType TRACK = builder.buildFeatureType();

        return TRACK;
	}

	@Override
	public Layer getLayer() {
		return wayPointLayer;
	}

	public Layer getTrackLayer(int index) {
		if(index>=0 && index<this.trackLayers.size()) {
			return this.trackLayers.get(index);
		} else {
			return null;
		}
	}
	@Override
	protected Style getStyle() {
		return wayPointStyle;
	}

	@Override
	protected String getXmlContent() {
		return "";
	}

	@Override
	public boolean fromXml() {
		// TODO Auto-generated method stub
		return false;
	}

	public Style getWayPointStyle() {
		return wayPointStyle;
	}

	public void setWayPointStyle(Style wayPointStyle) {
		this.wayPointStyle = wayPointStyle;
	}

	public GpxDocument getGpxDocument() {
		return gpxDocument;
	}

	public TrackSegment getFirstTrackSegment() {
		TrackSegment segment = null;
		if(this.gpxDocument.hasTracks()) {
			Track track = this.gpxDocument.getTrack(0);
			if(track.hasSegments()) {
				segment = track.getTrackSegment(0);
			}
		}
		return segment;
	}
}

package com.mlab.map;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.map.StyleLayer;
import org.geotools.styling.Style;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.mlab.gpx.api.WayPoint;
import com.mlab.gpx.impl.TrackSegment;
import com.mlab.map.factory.GeoToolsFactory;
import com.mlab.map.factory.StyleFac;
import com.mlab.map.layer.GpxLayer;
import com.mlab.map.layer.MobileLayer;
import com.mlab.map.layer.ShpLayer;
import com.mlab.patterns.AbstractObservable;
import com.vividsolutions.jts.linearref.LinearLocation;

public class TrackMapModel extends AbstractObservable {
	private final Logger LOG = Logger.getLogger(TrackMapModel.class); 
	
	public static final int TRACK_LINE_COLOR = Color.RED.getRGB();
	public static final int TRACK_LINE_WIDTH = 3;

	public static final int MOBILEPOINT_LINE_COLOR = Color.BLACK.getRGB();
	public static final int MOBILEPOINT_FILL_COLOR = Color.RED.getRGB();
	public static final int MOBILEPOINT_LINE_WIDTH = 1;
	public static final int MOBILEPOINT_SIZE = 12;

	
	protected MapContent mapContent;

	protected Layer baseLayer;
	protected List<ShpLayer> vectorLayers;
	protected GpxLayer trackLayer;
	protected MobileLayer mobileLayer;
	protected WayPoint lastPosition;

	protected ReferencedSegment refseg;
	
	// Constructor
	public TrackMapModel() {
		LOG.debug("MapModel()");
		System.setProperty("org.geotools.referencing.forceXY", "true");
		mapContent = new MapContent();
		ReferencedEnvelope limits = new ReferencedEnvelope(GeoToolsFactory.WORLD_ENVELOPE_EPSG_4326,GeoToolsFactory.getWGS84CRS());
		mapContent.getViewport().setBounds(limits);
		
		baseLayer = null;
		vectorLayers = new ArrayList<ShpLayer>();
		trackLayer = null;
		mobileLayer = null;
		lastPosition = null;
		refseg = null;
		
	}
	
	public MapContent getMapContent() {
		return mapContent;
	}
	public void setMapContent(MapContent mapContent) {
		this.mapContent = mapContent;
	}
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		if(mapContent != null && mapContent.getViewport() != null) {
			return mapContent.getViewport().getCoordinateReferenceSystem();			
		}
		LOG.error("getCoordinateReferenceSystem(): crs=null");
		return null;
	}
	public ReferencedEnvelope getMaxBounds() {
		return mapContent.getMaxBounds();
	}
	public MapViewport getViewPort() {
		return mapContent.getViewport();
	}
	public void setViewPort(MapViewport viewport) {
		mapContent.setViewport(viewport);
	}
	
	// Layers
	/**
	 * Número total de capas del mapa, incluyendo
	 * la capa base, las capas vectoriales, la 
	 * trackLayer y la mobileLayer
	 * 
	 * @return
	 */
	public int getLayerCount() {
		if (mapContent != null && mapContent.layers() != null) {
			return mapContent.layers().size();
		} else {
			return 0;
		}		
	}
	public Layer getLayer(int layerIndex) {
		if (mapContent != null && mapContent.layers() != null) {
			return mapContent.layers().get(layerIndex);
		}
		return null;
	}
	private boolean addLayer(Layer layer) {
		boolean result = false;
		if(mapContent != null && layer != null) { 
			result = mapContent.addLayer(layer);
			if(result) {
				//notifyObservers();
			} else {
				LOG.warn("addLayer(): Can't add layer, layer or mapContent null");
			}
		} else {
			LOG.warn("addLayer(): layer = null");
		}
		return result;
	}
	private boolean removeLayer(Layer layer) {
		boolean result = false;
		if(layer!=null && mapContent != null) { 
			result = mapContent.removeLayer(layer);
			if(result) {
				//notifyObservers();
			} else {
				LOG.warn("removeLayer(): Can't remove layer");
			}
		} else {
			LOG.warn("removeLayer(): Layer or mapContent are null");
		}
		return result;
	}
	private boolean moveLayer(Layer layer, int newpos) {
		boolean result = false;
		int pos = getLayerIndex(layer);
		if(pos >= 0 && newpos < mapContent.layers().size()) {
			mapContent.moveLayer(pos, newpos);
			result=true;
		} else {
			LOG.info("moveLayer(): Layer hasn't pos in mapContent");
		}
		return result;
	}
	public int getLayerIndex(Layer layer) {
		return mapContent.layers().indexOf(layer);		
	}
	public void release() {
		mapContent.dispose();
	}


	// Base Layer
	public Layer getBaseLayer() {
		return baseLayer;
	}
	public boolean setBaseLayer(Layer baselayer) {
		LOG.debug("TrackMapModel.setBaseLayer()");
		this.stopNotifications();
		removeBaseLayer();
		baseLayer = baselayer;		
		if(baseLayer == null) {
			LOG.warn("baselayer = null");
			return false;
		}
		boolean result = addLayer(baseLayer);
		if(result) {
			result = moveLayer(baseLayer, 0);
		} else {
			LOG.error("Can't add baseLayer");	
		}
		//this.setBaseLayerVisible(true);
		this.startNotifications();
		notifyObservers();
		return result;
	}
	public boolean removeBaseLayer() {
		LOG.debug("TrackMapModel.removeBaseLayer()");
		boolean result = false;
		if(baseLayer != null) {
			LOG.debug("TrackMapModel.removeBaseLayer() removing base layer " + baseLayer.getTitle());
			result = removeLayer(baseLayer);
			if(result) {
				baseLayer=null;
			} else {
				LOG.info("TrackMapModel.removeBaseLayer() WARNING: Can't remove baseLayer");
			}
		}
		return result;
	}	
	public void setBaseLayerVisible(boolean visible) {
		if(baseLayer != null) {
			baseLayer.setVisible(visible);
		}
	}
	
	// VectorLayers
	public boolean addVectorLayer(ShpLayer layer) {
		boolean result = false;
		result = addLayer(layer.getLayer());
		if(!result) {
			LOG.error("addVectorLayer() ERROR: Can't add Layer to mapContent");
			return false;
		}
		
		result = vectorLayers.add(layer);
		if(!result) {
			LOG.error("addVectorLayer() ERROR: Can't add Layer to vectorLayers");
			return false;
		}
		
		result = moveLayer(layer.getLayer(), lastVectorLayerIndex());
		if(!result) {
			LOG.error("addVectorLayer() ERROR: Can't move Layer to last position");
			return false;
		}
		notifyObservers();
		return true;
	}
	private int lastVectorLayerIndex() {
		int count = 0;
		if(baseLayer != null) {
			count++;
		}
		count += vectorLayers.size();
		return count-1;
	}
	
	// TrackLayer
	public boolean setTrackLayer(GpxLayer gpxlayer) {
		boolean result = false;
		removeTrackLayer();
		trackLayer = gpxlayer;
		if(trackLayer == null || trackLayer.getTrackLayer(0) == null) {
			LOG.info("setTrackLayer() WARNING trackLayer=null"); 
			return false;
		}
		result = addLayer(trackLayer.getTrackLayer(0));
		if(!result) {
			LOG.info("setTrackLayer() WARNING Can't add trackLayer"); 
			return false;
		}
		setTrackLayerStyle();
		setReferencedSegment();
		
		boolean result2 = setMobileLayer(trackLayer);
		if(!result2) {
			LOG.error("setTrackLayer(): Can't set mobileLayer") ;
			return false;
		}
		return true;		
	}
	public void setTrackLayerStyle() {
		if(this.trackLayer != null && trackLayer.getTrackLayer(0) != null) {
			StyleLayer layer = (StyleLayer)trackLayer.getTrackLayer(0);
			Color lineColor = new Color(TRACK_LINE_COLOR);
			Style style = StyleFac.createLineStyle(lineColor, TRACK_LINE_WIDTH);
			layer.setStyle(style);
		}
	}
	public void setTrackLayerStyle(Color color, int width) {
		if(this.trackLayer != null && trackLayer.getTrackLayer(0) != null) {
			Style style = StyleFac.createLineStyle(color, width);
			StyleLayer layer = (StyleLayer)trackLayer.getTrackLayer(0);
			layer.setStyle(style);			
		}
	}
	public void setTrackLayerStyle(Style style) {
		if(this.trackLayer != null && trackLayer.getTrackLayer(0) != null && style != null) {
			StyleLayer layer = (StyleLayer)trackLayer.getTrackLayer(0);
			layer.setStyle(style);			
		}
	}
	public boolean removeTrackLayer() {
		boolean result = false;
		if(trackLayer != null && trackLayer.getTrackLayer(0) != null) {
			result = removeLayer(trackLayer.getTrackLayer(0));
			if(result) {
				trackLayer = null;
			} else {
				LOG.debug("Can't remove trackLayer");
			}
		} else {
			//LOG.info("OldMapModel.removeTrackLayer() WARNING trackLayer null");
		}
		return result;
	}
	public GpxLayer getTrackLayer() {
		return trackLayer;
	}
	
	// ReferencedSegment
	private void setReferencedSegment() {
		refseg = new ReferencedSegment(trackLayer.getFirstTrackSegment());
	}
	public double getDistanceToOrigin() {
		if(refseg != null && mobileLayer != null) {
			LinearLocation loc = refseg.getLocation(mobileLayer.getX(), mobileLayer.getY());
			return refseg.getLength(loc);
		}
		return 0.0;
	}
		
	// MobileLayer
	public MobileLayer getMobileLayer() {
		return mobileLayer;
	}
	private boolean setMobileLayer(GpxLayer tracklayer) {
		boolean result = false;
		removeMobileLayer();
		TrackSegment segment = tracklayer.getFirstTrackSegment();
		if(segment == null) {
			LOG.error("segment=null");
			return false;
		}
		WayPoint startPoint = segment.getStartWayPoint();
		mobileLayer = createMobileLayer(startPoint);	
		result = addLayer(mobileLayer.getLayer());
		if(!result) {
			LOG.error("Can't add mobileLayer to trackMap"); 
			return false;
		}
		notifyObservers();
		return result;
	}
	private MobileLayer createMobileLayer(WayPoint wp) {
		double lon = 0.0;
		double lat = 0.0;
		if(wp!=null){
			lon = wp.getLongitude();
			lat = wp.getLatitude();
		}
		Color lineColor = new Color(MOBILEPOINT_LINE_COLOR);
		Color fillColor = new Color(MOBILEPOINT_FILL_COLOR);		
		Style style = StyleFac.createPointStyle(lineColor,MOBILEPOINT_LINE_WIDTH, 
				fillColor, MOBILEPOINT_SIZE);
		MobileLayer layer = new MobileLayer("MobileLayer",GeoToolsFactory.getWGS84CRS(), lat,lon);
		layer.setStyle(style);
		return layer;
	}
	public void setMobileLayerStyle(Style style) {
		if(this.mobileLayer != null) {
			this.mobileLayer.setStyle(style);
		}
	}
	public boolean removeMobileLayer() {
		boolean result = false;
		if(mobileLayer != null) {
			result = removeLayer(mobileLayer.getLayer());
			if(result) {
				mobileLayer=null;
			} else {
				LOG.warn("Can't remove mobileLayer "+mobileLayer.getName());
			}
		}
		notifyObservers();
		return result;
	}
	public double[] getMobilePositionLonLat() {
		double[] latlon = null;
		if(mobileLayer != null) {
			return new double[]{mobileLayer.getX(), mobileLayer.getY()};
		}
		return latlon;
	}
	public double[] getMobilePositionXY() {
		double[] xy = null;
		double[] latlon = this.getMobilePositionLonLat();
		if(latlon != null) {
			Envelope2D env = new Envelope2D(GeoToolsFactory.getWGS84CRS(),latlon[0],latlon[0],latlon[1],latlon[1]);
			Envelope2D envproj = null;
			try {
				envproj = (Envelope2D) env.toBounds(getCoordinateReferenceSystem());
				xy = new double[] {envproj.getMinX(), envproj.getMinY()};
			} catch (Exception e) {
				LOG.error("getMobilePositionXY() ERROR: Can't reproject");
			}
		}
		return xy;
	}
	public void setMobilePositionLatLon(WayPoint wp) {
		//LOG.debug("TrackMapModel.setMobilePositionLonLat()");
		this.setLastPosition(wp);
		if(mobileLayer!=null) {
			mobileLayer.move(wp.getLongitude(), wp.getLatitude());			
		}

		notifyObservers();
	}
	public void setLastPosition(WayPoint lastpos) {
		this.lastPosition = lastpos.clone();
	}
	public WayPoint getLastPosition() {
		return this.lastPosition;
	}
	
}

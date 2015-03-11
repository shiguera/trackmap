package com.mlab.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.apache.log4j.Logger;
import org.geotools.map.Layer;
import org.geotools.map.MapViewport;
import org.geotools.styling.Style;

import com.mlab.gpx.api.WayPoint;
import com.mlab.map.factory.GeoToolsFactory;
import com.mlab.map.factory.WMSFactory;
import com.mlab.map.layer.GpxLayer;
import com.mlab.map.layer.ShpLayer;

public class TrackMap implements ActionListener {
	private final Logger LOG = Logger.getLogger(TrackMap.class);
	
	protected TrackMapModel mapModel;
	protected MapView view;
	
	public TrackMap(TrackMapModel model, MapView view) {
		this.mapModel = model;
		this.view = view;
	}
	
	public TrackMap(TrackMapModel model) {
		this.mapModel = model;		
	}

	public TrackMapModel getMapModel() {
		return mapModel;
	}

	

	// BaseLayer
	public void setBaseLayerVisible(boolean visible) {
		mapModel.setBaseLayerVisible(visible);
	}
	public void setDefaultBaseLayer() {
		mapModel.setBaseLayer(WMSFactory.getProxyLayer(2));
		mapModel.getViewPort().setBounds(mapModel.getMaxBounds());
	}
	// VectorLayers
	public boolean addVectorLayer(ShpLayer layer) {
		boolean result = mapModel.addVectorLayer(layer);
		LOG.debug("LayeredMapController.addVectorLayer() "+result);
		if(result) {
			//zoomLayer(layer.getLayer());
		} else {
			LOG.warn("Can't add vector layer"+layer.getName());
		}
		return result;
	}
	
	public boolean setTrackLayer(File gpxfile) {
		GpxLayer gpxlayer = GeoToolsFactory.createGpxLayer(gpxfile);
		if(gpxlayer == null || gpxlayer.getFirstTrackSegment() == null || gpxlayer.getFirstTrackSegment().size()<2) {
			LOG.warn("setTrackLayer() WARNING no valid gpxlayer") ;
			return false;
		}
		boolean result = mapModel.setTrackLayer(gpxlayer);
		if(!result) {
			LOG.error("setTrackLayer(): Can't set trackLayer") ;
			return false;
		}

		if(result) {
			setBaseLayerVisible(false);
			zoomTrack();
			//setCenterToMobile();
			setBaseLayerVisible(true);
		}
		return result;
		
	}
//	public boolean openTrackSegmentFile(TrackSegmentFile tsfile) {
//		GpxLayer gpxlayer = MapFactory.createGpxLayer(tsfile.getGpxDocument());
//		if(gpxlayer == null || gpxlayer.getFirstTrackSegment() == null || gpxlayer.getFirstTrackSegment().size()<2) {
//			LOG.warn("openTrackSegmentFile() WARNING no valid gpxlayer") ;
//			return false;
//		}
//		boolean result = mapModel.setTrackLayer(gpxlayer);
//		if(!result) {
//			LOG.error("openTrackSegmentFile(): Can't set trackLayer") ;
//			return false;
//		}
//
//		if(result) {
//			setBaseLayerVisible(false);
//			zoomTrack();
//			//setCenterToMobile();
//			setBaseLayerVisible(true);
//		}
//		return result;
//	}
	public void setTrackLayerStyle(Style style) {
		mapModel.setTrackLayerStyle(style);
	}
	public void setMobileLayerStyle(Style style) {
		this.mapModel.setMobileLayerStyle(style);
	}
	public void setMobilePositionLatLon(WayPoint wp) {
		//LOG.debug("LayeredMapController.setMobilePositionLonLat()");
		mapModel.setMobilePositionLatLon(wp);
		//mapView.refreshMap();
	}
	
	public void setViewPort(MapViewport viewport) {
		mapModel.setViewPort(viewport);
	}
	
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent ev) {
		//LOG.debug("actionPerformed()");
	
	}

	public void zoomExtent() {
		LOG.debug("zoomExtent()");
		mapModel.setViewPort(new MapViewport(mapModel.getMaxBounds()));
	}

	public void zoomTrack() {
		LOG.debug("zoomTrack() TODO not implemented");
	}
	
	public void release() {
		mapModel.release();
	}

	public MapView getView() {
		return view;
	}

	public void setView(MapView view) {
		this.view = view;
	}
	
}

package com.mlab.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapViewport;
import org.geotools.styling.Style;

import com.mlab.gpx.api.WayPoint;
import com.mlab.map.factory.GeoToolsMapFactory;
import com.mlab.map.factory.MapFactory;
import com.mlab.map.factory.WMSFactory;
import com.mlab.map.layer.GpxLayer;
import com.mlab.map.layer.ShpLayer;

public class TrackMap implements ActionListener {
	private final Logger LOG = Logger.getLogger(TrackMap.class);
	
	protected TrackMapModel mapModel;
	protected MapView mapView;
	
	public TrackMap(TrackMapModel model) {
		this.mapModel = model;		
	}
	public void setMapView(MapView mapview) {
		LOG.debug("setMapView()");
		this.mapView = mapview;
		this.mapView.setMapContent(this.mapModel.getMapContent());
		//this.mapView.setZoomSpainAction(new NewZoomSpainAction(this));
		//this.mapView.setZoomTrackAction(new ZoomTrackAction(this));
		
	}
	public TrackMapModel getMapModel() {
		return mapModel;
	}

	public MapView getMapView() {
		return mapView;
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
			zoomLayer(layer.getLayer());
		} else {
			LOG.warn("Can't add vector layer"+layer.getName());
		}
		return result;
	}
	
	public boolean setTrackLayer(File gpxfile) {
		GpxLayer gpxlayer = GeoToolsMapFactory.createGpxLayer(gpxfile);
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
		mapView.refreshMap();
	}
	
	public void setViewPort(MapViewport viewport) {
		mapModel.setViewPort(viewport);
	}
	
	// ActionListener
	@Override
	public void actionPerformed(ActionEvent ev) {
		//LOG.debug("actionPerformed()");
		if(ev.getActionCommand().equals("ZOOM_SPAIN")) {
			//LOG.debug("actionPerformed(): " + ev.getActionCommand());
			zoomSpain();
		} else if(ev.getActionCommand().equals("ZOOM_TRACK")) {
			//LOG.debug("actionPerformed(): " + ev.getActionCommand());
			zoomTrack();
		}
	}

	public void zoomSpain() {
		if(mapModel==null || mapView==null) {
			return;
		}
	    ReferencedEnvelope spainBounds = GeoToolsMapFactory.getSpainEpsg4326Bounds();
        ReferencedEnvelope sp = null;
        try {
        	//LOG.debug("mapModel.CRS=" + mapModel.getCoordinateReferenceSystem().getName());
			sp = new ReferencedEnvelope(spainBounds.transform(mapModel.getCoordinateReferenceSystem(), true));
			//LOG.debug(mapModel.getViewPort().getBounds());
			mapView.setDisplayArea(sp);
			//LOG.debug(mapModel.getViewPort().getBounds());
        } catch (Exception e) {
			LOG.warn("zoomSpain(): Can't reproyect Spain geo envelope"+spainBounds.toString());
			LOG.warn(e.getMessage());
        } 
	}

	public void zoomTrack() {
		LOG.debug("zoomTrack() TODO not implemented");
	}
	
	public void zoomLayer(Layer layer) {
		ReferencedEnvelope env = layer.getBounds();
		if(env.getCoordinateReferenceSystem() == null) {
			LOG.warn("zoomLayer(): Can't zoom layer, it doesn't have CoordinateReferenceSystem");
			return;			
		}
		//LOG.trace(env.getCoordinateReferenceSystem().getName().toString());
		ReferencedEnvelope env2=null;
		try {
			env2 = env.transform(mapModel.getCoordinateReferenceSystem(), true);
			if(mapView != null) {
				//LOG.debug("zoomLayer() before "+env2.toString());
				mapView.setDisplayArea(env2);
			}
		} catch (Exception e) {
			LOG.warn("zoomLayer(): Can't reproject layer");
			return;
		}
	}
	public void release() {
		mapModel.release();
	}
	
}

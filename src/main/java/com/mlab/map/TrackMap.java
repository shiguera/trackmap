package com.mlab.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.apache.log4j.Logger;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapViewport;
import org.geotools.map.WMSLayer;
import org.geotools.styling.Style;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.MathTransform;

import com.mlab.gpx.api.WayPoint;
import com.mlab.gpx.impl.GpxEnvelope;
import com.mlab.gpx.impl.TrackSegment;
import com.mlab.map.factory.GeoToolsFactory;
import com.mlab.map.factory.WMSFactory;
import com.mlab.map.layer.GpxLayer;
import com.mlab.map.layer.ShpLayer;
import com.mlab.map.swing.MapView;

public class TrackMap implements ActionListener {
	private final Logger LOG = Logger.getLogger(TrackMap.class);
	
	protected TrackMapModel mapModel;
	protected MapView view;
	protected GeoToolsFactory factory;
	
	public TrackMap(TrackMapModel model, MapView view) {
		this.mapModel = model;
		this.view = view;
		
		factory = new GeoToolsFactory();
	}
	
	public TrackMap(TrackMapModel model) {
		this(model, null);		
	}

	public TrackMapModel getMapModel() {
		return mapModel;
	}

	

	// BaseLayer
	public Layer getBaseLayer() {
		return mapModel.baseLayer;
	}
	public boolean setBaseLayer(Layer layer) {
		boolean result = mapModel.setBaseLayer(layer);
		if (result && view != null) {
			view.refreshMap();
			//mapModel.getViewPort().setBounds(mapModel.getMaxBounds());
		}
		return result;
	}
	public void setBaseLayerVisible(boolean visible) {
		mapModel.setBaseLayerVisible(visible);
	}
	public void setDefaultBaseLayer() {
		mapModel.setBaseLayer(WMSFactory.getIGNBaseLayer());
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
		if (view != null) {
			view.refreshMap();			
		}
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
		if(view != null) {
			view.getJMapPane().reset();			
			view.refreshMap();
		}
	}
	public void zoomSpain() {
	    ReferencedEnvelope geo = GeoToolsFactory.getSpainEpsg4326Bounds();
        ReferencedEnvelope spainEnv = null;
        try {
			spainEnv = new ReferencedEnvelope(
					geo.transform(mapModel.getCoordinateReferenceSystem(), true));
			if(view != null) {
				view.getJMapPane().setDisplayArea(spainEnv);
				view.refreshMap();
			}
        } catch (Exception e) {
			LOG.warn("zoomSpain() ERROR: Can't reproyect Spain geo envelope"+geo.toString());
			LOG.warn(e.getMessage());
        } 
	}
	public void zoomTrack() {
		LOG.debug("zoomTrack()");
		GpxLayer trackLayer = mapModel.getTrackLayer();
		if(trackLayer!=null && trackLayer.getFirstTrackSegment()!=null) {
			TrackSegment segment = trackLayer.getFirstTrackSegment();
			// Calcular diagonal del track
			GpxEnvelope env = segment.getEnvelope();
			DirectPosition pos1 = new DirectPosition2D(env.getMinLon(), env.getMinLat());
			DirectPosition pos2 = new DirectPosition2D(env.getMaxLon(), env.getMaxLat());
			DirectPosition ppos1=null, ppos2=null;
			MathTransform tr = GeoToolsFactory.generateTransformFromWGS84(mapModel.getCoordinateReferenceSystem());
			if (tr==null) {
				return;
			}
			try {
				ppos1 = tr.transform(pos1, ppos1);
				ppos2 = tr.transform(pos2, ppos2);
			} catch (Exception e) {
				LOG.warn("zoomTrack(): Can't reproject point "+e.getMessage());
				return;
			} 
			
			Envelope2D env2 = new Envelope2D();
			env2.setFrameFromDiagonal(ppos1.getOrdinate(0), ppos1.getOrdinate(1), 
					ppos2.getOrdinate(0), ppos2.getOrdinate(1));
			if(view != null) {
				view.getJMapPane().setDisplayArea(env2);
				view.refreshMap();
			}			
		}
	}
	public void zoomLayer(Layer layer) {
		ReferencedEnvelope env = layer.getBounds();
		if(env.getCoordinateReferenceSystem() == null) {
			LOG.warn("zoomLayer(): Can't zoom layer, it doesn't have CoordinateReferenceSystem");
			return;			
		}
		//LOG.trace(env.getCoordinateReferenceSystem().getName().toString());
		ReferencedEnvelope env2 = null;
		try {
			env2 = env.transform(mapModel.getCoordinateReferenceSystem(), true);
			if(view != null) {
				//LOG.debug("zoomLayer() before "+env2.toString());
				view.setDisplayArea(env2);
			}
		} catch (Exception e) {
			LOG.warn("zoomLayer(): Can't reproject layer");
			return;
		}
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

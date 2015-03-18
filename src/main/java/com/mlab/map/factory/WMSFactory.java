package com.mlab.map.factory;

import java.net.URI;
import java.net.URL;

import org.apache.log4j.Logger;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.map.WMSLayer;

public class WMSFactory {

	private final static Logger LOG = Logger.getLogger(WMSFactory.class);
	
	// WMSLayers
	public static WMSLayer getProxyLayer(int layernum) {
		URL url = null;
		WebMapServer wms = null;
		try {
	        //url = new URL("http://osm.omniscale.net/proxy/service");
	        URI uri = new URI("http://82.223.107.55:8080/service/?");
	        url = uri.toURL();
	        //System.out.println(url.getHost());
	        //System.out.println(url.getProtocol());
	       	wms = new WebMapServer(url);        
		} catch (Exception e) {
			GeoToolsFactory.LOG.warn("WMSFactory.getProxyLayer() ERROR: can't create wms\n"+e.getMessage());
			return null;
		}
	    WMSCapabilities capabilities = wms.getCapabilities();
	    org.geotools.data.ows.Layer[] list = WMSUtils.getNamedLayers(capabilities);
	    
		WMSLayer displayLayer = new WMSLayer(wms, list[layernum]);
	    return displayLayer;
	}
	public static WMSLayer getProxyOSMLayer() {
		return WMSFactory.getProxyLayer(0);
	}
	public static WMSLayer getProxyIGNBaseLayer() {
		return WMSFactory.getProxyLayer(1);
		
	}
	public static WMSLayer getProxyPNOALayer() {
		return WMSFactory.getProxyLayer(2);		
	}

	
	public static WMSLayer getGoogleLayer() {
		URL url = null;
		WebMapServer wms = null;
		try {
	        url = new URL("https://mapsengine.google.com/17606346560721475660-16071188762309719429-4/wms/?version=1.3.0&SERVICE=WMS&REQUEST=GetCapabilities");
	       	wms = new WebMapServer(url);        
	        System.out.println(wms.toString());
		} catch (Exception e) {
			GeoToolsFactory.LOG.warn("GeoToolsFactory.getGoogleLayer() ERROR: can't create wms\n"+e.getMessage());
			return null;
		}
	    WMSCapabilities capabilities = wms.getCapabilities();
	    org.geotools.data.ows.Layer[] list = WMSUtils.getNamedLayers(capabilities);
	    for(int i=0; i<list.length; i++) {
	    	System.out.println("layer "+i+": "+list[i].getName());
	    }
		WMSLayer displayLayer = new WMSLayer(wms, list[0]);
	    return displayLayer;
	}
	public static WMSLayer getIGNBaseLayer() {
		URL url = null;
		WebMapServer wms = null;
		try {
	        url = new URL("http://www.ign.es/wms-inspire/ign-base?SERVICE=WMS&");
	       	wms = new WebMapServer(url);        
	        System.out.println(wms.toString());
		} catch (Exception e) {
			GeoToolsFactory.LOG.warn("GeoToolsFactory.getIGNLayer() ERROR: can't create wms\n"+e.getMessage());
			return null;
		}
	    WMSCapabilities capabilities = wms.getCapabilities();
	    org.geotools.data.ows.Layer[] list = WMSUtils.getNamedLayers(capabilities);
//	    for(int i=0; i<list.length; i++) {
//	    	System.out.println("layer "+i+": "+list[i].getName());
//	    }
		WMSLayer displayLayer = new WMSLayer(wms, list[24]);
	    return displayLayer;
	}
	public static WMSLayer getWMSLayer(WMSDescriptor wmsDescriptor) {
		URL url = null;
		WebMapServer wms = null;
		try {
	        url = new URL(wmsDescriptor.getUrl());
	       	wms = new WebMapServer(url);        
	        //System.out.println(wms.toString());
		} catch (Exception e) {
			LOG.warn("WMSFactory.getWMSLayer() ERROR: can't create wms\n"+e.getMessage());
			return null;
		}
	    WMSCapabilities capabilities = wms.getCapabilities();
	    org.geotools.data.ows.Layer[] list = WMSUtils.getNamedLayers(capabilities);
		WMSLayer displayLayer = new WMSLayer(wms, list[wmsDescriptor.getLayerNum()]);
	    return displayLayer;
	}
}

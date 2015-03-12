package com.mlab.map;

import junit.framework.Assert;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.WMSLayer;
import org.junit.Test;

import com.mlab.map.factory.WMSFactory;

public class TestTrackMap {

	/**
	 * Es posible crear un TrackMap sin MapView y operar con él
	 */
	@Test
	public void testNullMapView() {
		System.out.println("TestTrackMap.testNullMapView()");
		TrackMapModel model = new TrackMapModel();
		Assert.assertNotNull(model);
		
		TrackMap map = new TrackMap(model, null);
		Assert.assertNotNull(map);
		
	}
	@Test
	public void setBaseMapReturnsTrue() {
		System.out.println("TestTrackMap.setBaseMapReturnsTrue()");
		TrackMapModel model = new TrackMapModel();
		Assert.assertNotNull(model);
		
		TrackMap map = new TrackMap(model, null);
		Assert.assertNotNull(map);
		
		WMSLayer layer = WMSFactory.getIGNLayer();
		boolean result = map.setBaseLayer(layer);
		Assert.assertTrue(result);
		
	}
	@Test
	public void setBaseMapSetsTheMap() {
		System.out.println("TestTrackMap.setBaseMapSetsTheMap()");
		TrackMapModel model = new TrackMapModel();
		Assert.assertNotNull(model);
		
		TrackMap map = new TrackMap(model, null);
		Assert.assertNotNull(map);
		
		WMSLayer orglayer = WMSFactory.getIGNLayer();
		ReferencedEnvelope orgenv = orglayer.getBounds();
		Assert.assertTrue(map.setBaseLayer(orglayer));
		
		Layer savedlayer = map.getBaseLayer();
		Assert.assertNotNull(savedlayer);
		ReferencedEnvelope savedenv = savedlayer.getBounds();
		Assert.assertEquals(orgenv.getCoordinateReferenceSystem().getName().toString(), 
				savedenv.getCoordinateReferenceSystem().getName().toString());
		Assert.assertEquals(orgenv.getMinX(), savedenv.getMinX());
		Assert.assertEquals(orgenv.getMinY(), savedenv.getMinY());
		Assert.assertEquals(orgenv.getMaxX(), savedenv.getMaxX());
		Assert.assertEquals(orgenv.getMaxY(), savedenv.getMaxY());
	}

}

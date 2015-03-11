package com.mlab.map;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.mlab.map.factory.GTMapFactory;
import com.mlab.map.factory.GeoToolsFactory;
import com.mlab.map.factory.MapFactory;
import com.mlab.map.layer.ShpLayer;

public class TestTrackMapModel {

	private TrackMapModel model;
	private MapFactory mapFactory;
	private ShpLayer shpLayer;
	 
	@Before
	public void before() {
		model = new TrackMapModel();
		Assert.assertNotNull(model);
		mapFactory = new GTMapFactory();
		loadShapelayer();
	}
	private void loadShapelayer() {
		File file = new File(ClassLoader.getSystemResource("Distrtitos_4326.shp").getFile());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		ShpLayer shplayer = mapFactory.createShpLayer(file);
		Assert.assertNotNull(shplayer);		
	}
	@Test
	public void test() {
		System.out.println("TestTrackMapModel.test()");
		
		Assert.assertNull(model.getBaseLayer());
		Assert.assertEquals(0, model.getLayerCount());
		Assert.assertNull(model.getTrackLayer());
		Assert.assertNull(model.getMobileLayer());
		Assert.assertNull(model.getLastPosition());
		Assert.assertNull(model.refseg);
	}
	@Test
	public void crsIsWgs84CrsAfterCreation() {
		System.out.println("TestTrackMapModel.crsIsWgs84CrsAfterCreation()");
		Assert.assertNotNull(model.getCoordinateReferenceSystem());
		Assert.assertEquals("EPSG:WGS 84", model.getCoordinateReferenceSystem().getName().toString());
	}
	@Test
	public void viewPortAfterCreation() {
		System.out.println("TestTrackMapModel.viewPortAfterCreation()");
		Assert.assertNotNull(model.getViewPort());
		Assert.assertEquals("EPSG:WGS 84", model.getCoordinateReferenceSystem().getName().toString());
		
		Assert.assertEquals(-179.99, model.getViewPort().getBounds().getMinX(), 0.01);
		Assert.assertEquals(179.99, model.getViewPort().getBounds().getMaxX(), 0.01);
		Assert.assertEquals(-89.99, model.getViewPort().getBounds().getMinY(), 0.01);
		Assert.assertEquals(89.99, model.getViewPort().getBounds().getMaxY(), 0.01);
		
	}

	@Test
	public void mapViewPortNotNullAfterCreation() {
		System.out.println("TestTrackMapModel.mapViewPortNotNullAfterCreation()");
		Assert.assertNotNull(model.getViewPort());
	}
}

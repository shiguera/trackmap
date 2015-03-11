package com.mlab.map.action;

import java.io.File;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mlab.map.TrackMap;
import com.mlab.map.TrackMapModel;
import com.mlab.map.factory.GTMapFactory;
import com.mlab.map.layer.ShpLayer;

public class TestZoomExtentAction {

	static TrackMapModel model;
	static TrackMap map;
	static GTMapFactory mapFactory;
	static ShpLayer shplayer;
	
	@BeforeClass
	public static void setupModel() {
		PropertyConfigurator.configure("log4j.properties");
		model = new TrackMapModel();
		Assert.assertNotNull(model);
		map = new TrackMap(model, null);
		Assert.assertNotNull(map);
		mapFactory = new GTMapFactory();
		loadShapelayer();
	}
	static void loadShapelayer() {
		File file = new File(ClassLoader.getSystemResource("Distrtitos_4326.shp").getFile());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		shplayer = mapFactory.createShpLayer(file);
		Assert.assertNotNull(shplayer);		
		model.addVectorLayer(shplayer);
	}
	
	@Test
	public void afterZoomExtentsMaxBoundsAreEqualsLayer() {		
		System.out.println("TestZoomExtentAction.afterZoomExtentsMaxBoundsAreEqualsLayer()");
		ZoomExtentAction zeaction = new ZoomExtentAction(map);
		zeaction.actionPerformed(null);
		ReferencedEnvelope env = model.getMaxBounds();
		ReferencedEnvelope env2 = shplayer.getLayer().getBounds();
		Assert.assertEquals(env.getMaxX(), env2.getMaxX(), 0.001);
		Assert.assertEquals(env.getMinX(), env2.getMinX(), 0.001);
		Assert.assertEquals(env.getMaxY(), env2.getMaxY(), 0.001);
		Assert.assertEquals(env.getMinY(), env2.getMinY(), 0.001);
		
	}

}

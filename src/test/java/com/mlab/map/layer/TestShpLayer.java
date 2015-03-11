package com.mlab.map.layer;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TestShpLayer {

	@Test
	public void test() {
		System.out.println("Testing TestShpLayer()");
		
		ShpLayer sampleLayer1 = loadTestLayer();

	}
	@Test
	public void testSetName() {
		System.out.println("TestSetName()");
		ShpLayer sampleLayer1 = loadTestLayer();
		System.out.println("layerName="+sampleLayer1.getName());
		Assert.assertEquals("test.shp", sampleLayer1.getName());
		Assert.assertEquals("test.shp", sampleLayer1.getLayer().getTitle());
		
		System.out.println("OK");
		
	}
	
	@Test
	public void testCrsAfterLoad() {
		System.out.println("TestShpLayer.testCrsAfterLoad()");
		ShpLayer layer = loadTestLayer();
		System.out.println(layer.getLayer().getBounds().getCoordinateReferenceSystem());
		System.out.println(layer.getLayer().getBounds().getLowerCorner());
		System.out.println(layer.getLayer().getBounds().getUpperCorner());
		Assert.assertEquals("EPSG:WGS 84", layer.getLayer().getBounds().getCoordinateReferenceSystem().getName().toString());
		Assert.fail();
	}
	
	private ShpLayer loadTestLayer() {
		String layerUrl = "test.shp";
		URL url = ClassLoader.getSystemResource(layerUrl);
		File file = new File(url.getPath());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer sampleLayer1 = new ShpLayer(file);
		Assert.assertNotNull(sampleLayer1);
		Assert.assertNotNull(sampleLayer1.getLayer());
		return sampleLayer1;		
	}

}

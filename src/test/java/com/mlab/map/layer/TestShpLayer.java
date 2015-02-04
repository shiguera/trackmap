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
		URL url = ClassLoader.getSystemResource("test.shp");
		File file = new File(url.getPath());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer sampleLayer1 = new ShpLayer(file);
		Assert.assertNotNull(sampleLayer1);
		Assert.assertNotNull(sampleLayer1.getLayer());
		try {
			CoordinateReferenceSystem crs = CRS.decode("EPSG:3857");
			System.out.println(crs.toWKT());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("OK");
	}
	@Test
	public void testSetName() {
		System.out.println("TestSetName()");
		String layerUrl = "test.shp";
		URL url = ClassLoader.getSystemResource(layerUrl);
		File file = new File(url.getPath());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer sampleLayer1 = new ShpLayer(file);
		Assert.assertNotNull(sampleLayer1);
		Assert.assertNotNull(sampleLayer1.getLayer());
		
		System.out.println("layerName="+sampleLayer1.getName());
		Assert.assertEquals("test.shp", sampleLayer1.getName());
		Assert.assertEquals("test.shp", sampleLayer1.getLayer().getTitle());
		
		System.out.println("OK");
		
	}

}

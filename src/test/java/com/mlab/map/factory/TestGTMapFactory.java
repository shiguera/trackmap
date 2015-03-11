package com.mlab.map.factory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import com.mlab.map.factory.GTMapFactory;
import com.mlab.map.factory.MapFactory;
import com.mlab.map.layer.ShpLayer;

public class TestGTMapFactory {

	@Test
	public void testCreateShapeFile() {
		System.out.println("TestGTMapFactory.testCreateShapeFile()");
		MapFactory factory = new GTMapFactory();
		Assert.assertNotNull(factory);
		
		File file = this.getShapeFileFromResources();
		Assert.assertNotNull(file);
		
		ShpLayer layer = factory.createShpLayer(file);
		Assert.assertNotNull(layer);
		
		file = null;
		layer = factory.createShpLayer(file);
		Assert.assertNull(layer);
		
		file = new File("noexistefile.shp");
		layer = factory.createShpLayer(file);
		Assert.assertNull(layer);
		
	}
	private File getShapeFileFromResources() {
		URL url = ClassLoader.getSystemResource("CallejeroPol.shp");
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			Assert.fail("getShapeFileFromResources(): Can't read file from resources");			
		}
		return file;
	}

}

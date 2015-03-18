package com.mlab.map.factory;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.geotools.map.WMSLayer;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestWMSFactory {

	@BeforeClass
	public static void setup() {
		PropertyConfigurator.configure("log4j.properties");
	}
	@Test
	public void getWMSReturnsNotNullLayer() {
		System.out.println("test()");
		String url = "http://www.ign.es/wms-inspire/ign-base?SERVICE=WMS&";
		String desc = "IGNBase";
		int layernum = 24;
		WMSDescriptor wms = new WMSDescriptor(desc, url, layernum);
		WMSLayer layer = WMSFactory.getWMSLayer(wms);
		Assert.assertNotNull(layer);
	}

}

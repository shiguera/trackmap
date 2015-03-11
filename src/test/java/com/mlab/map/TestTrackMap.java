package com.mlab.map;

import junit.framework.Assert;

import org.junit.Test;

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
	public void test() {
		System.out.println("TestTrackMap.test()");

	}
}

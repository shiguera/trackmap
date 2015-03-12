package com.mlab.map;

import junit.framework.Assert;

import org.junit.Test;

import com.mlab.map.swing.BaseMapSelector;

public class TestBaseMapSelector {

	@Test
	public void baseMapSelectorIsEmptyAfterCreation() {
		System.out.println("TestBaseMapSelector.baseMapSelectorIsEmptyAfterCreation()");
		BaseMapSelector bms = new BaseMapSelector();
		Assert.assertNotNull(bms);
		Assert.assertEquals(0, bms.getItemCount());
	}
	
	@Test
	public void canAddUrl() {
		System.out.println("TestBaseMapSelector.canAddUrl()");
		BaseMapSelector bms = new BaseMapSelector();
		Assert.assertNotNull(bms);
		bms.addItem("http://mercatorlab.com");
		Assert.assertEquals(1, bms.getItemCount());
	}
}

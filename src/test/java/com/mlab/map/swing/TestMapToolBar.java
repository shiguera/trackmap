package com.mlab.map.swing;

import javax.swing.JButton;

import junit.framework.Assert;

import org.junit.Test;

import com.mlab.map.TrackMap;
import com.mlab.map.TrackMapModel;

public class TestMapToolBar {

	@Test
	public void setDefaultButtonsIsOk() {
		System.out.println("TestMapToolBar.setDefaultButtonsIsOk()");
		TrackMapModel model = new TrackMapModel();
		TrackMap map = new TrackMap(model);
		MapToolBar toolbar = new MapToolBarImpl();
		Assert.assertNotNull(toolbar);
				
		MapView view = new SingleMapView(model);
		
		toolbar.setDefaultButtons(map, view.getJMapPane());
		Assert.assertEquals(6, toolbar.getButtonsCount());
		
		JButton btn = toolbar.getButton(0);
		Assert.assertNotNull(btn);
		
	}

}

package com.mlab.map.swing;

import java.awt.Component;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mlab.map.TrackMap;
import com.mlab.map.TrackMapModel;

public class TestSingleMapView {
	
	static TrackMapModel model;
	
	@BeforeClass
	public static void setupModel() {
		PropertyConfigurator.configure("log4j.properties");
		model = new TrackMapModel();
		Assert.assertNotNull(model);
	}

	@Test
	public void mapViewAndComponentsAreNotNullWithMapContentNull() {
		System.out.println("TestSingleMapView.mapViewAndComponentsAreNotNullWithMapContentNull()");
		MapView view = new SingleMapView(model);
		Assert.assertNotNull(view);
		Assert.assertNotNull(view.getMainPanel());
		Assert.assertNotNull(view.getMapPanel());
		Assert.assertNotNull(view.getJMapPane());		
	}
	@Test
	public void toolBarIsNotNullAfterAdded() {
		System.out.println("TestSingleMapView.toolBarIsNotNullAfterAdded()");
		MapView view = new SingleMapView(model);
		MapToolBar toolbar = new MapToolBarImpl();
		toolbar.setDefaultButtons(new TrackMap(model), view.getJMapPane());
		view.setMapToolBar(toolbar);
		Assert.assertNotNull(view.getMapToolBar());
	}
	@Test
	public void toolBarIsAComponentOfMainPanelAfterAdded() {
		System.out.println("TestSingleMapView.toolBarIsAComponentOfMainPanelAfterAdded()");
		MapView view = new SingleMapView(model);
		MapToolBar toolbar = new MapToolBarImpl();
		toolbar.setDefaultButtons(new TrackMap(model), view.getJMapPane());
		view.setMapToolBar(toolbar);
		boolean result = false;
		for(Component c: view.getMainPanel().getComponents()) {
			if(c.equals(toolbar)) {
				result = true;
				break;
			}
		}
		Assert.assertTrue(result);
	}
}

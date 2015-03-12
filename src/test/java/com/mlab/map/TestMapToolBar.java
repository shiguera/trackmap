package com.mlab.map;

import javax.swing.JButton;

import junit.framework.Assert;

import org.junit.Test;

import com.mlab.map.swing.MapToolBar;
import com.mlab.map.swing.MapToolBarImpl;
import com.mlab.map.swing.MapView;
import com.mlab.map.swing.SingleMapView;

public class TestMapToolBar {

	@Test
	public void setDefaultButtonsIsOk() {
		System.out.println("TestMapToolBar.setDefaultButtonsIsOk()");
		MapToolBar toolbar = new MapToolBarImpl();
		Assert.assertNotNull(toolbar);
				
		MapView view = new SingleMapView(null);
		
		toolbar.setDefaultButtons(view.getJMapPane());
		Assert.assertEquals(3, toolbar.getButtonsCount());
		
		JButton btn = toolbar.getButton(0);
		Assert.assertNotNull(btn);
		
	}

}

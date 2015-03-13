package com.mlab.map.swing;

import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mlab.map.TrackMap;
import com.mlab.map.TrackMapModel;

public class TestBaseMapCombo {

	private static TrackMap trackMap;
	
	@BeforeClass
	public static void setup() {
		PropertyConfigurator.configure("log4j.properties");
		trackMap = new TrackMap(new TrackMapModel());
		Assert.assertNotNull(trackMap);
	}
	@Test
	public void comboIsEmptyAfterCreation() {
		System.out.println("TestBaseMapCombo.comboIsEmptyAfterCreation()");
		BaseMapCombo bms = new BaseMapCombo(trackMap);
		Assert.assertNotNull(bms);
		Assert.assertEquals(0, bms.getItemCount());
	}
	@Test
	public void comboHasElementsIfConstructorWithArrayIsUsed() {
		System.out.println("TestBaseMapCombo.comboHasElementsIfConstructorWithArrayIsUsed()");
		ArrayList<WMSDescriptor> wms = new ArrayList<WMSDescriptor>();
		wms.add(new WMSDescriptor("Prueba 1", "Url 1",0));
		wms.add(new WMSDescriptor("Prueba 2", "Url 2",0));
				
		WMSDescriptor[] descs = asArray(wms);
		//JComboBox bms = new JComboBox(new BaseMapSelectorModel(descs));
		BaseMapCombo bms = new BaseMapCombo(trackMap, descs);
		bms.addActionListener(bms);
		Assert.assertNotNull(bms);
		Assert.assertEquals(2, bms.getItemCount());
		bms.addItem(new WMSDescriptor("Prueba 3", "Url 3",0));
		Assert.assertEquals(3, bms.getItemCount());
		WMSDescriptor sel = (WMSDescriptor)bms.getSelectedItem();
		Assert.assertEquals("Prueba 1", sel.getDescription());
		Assert.assertEquals("Url 1", sel.getUrl());
		System.out.println("going to select index");
		bms.setSelectedIndex(2);
		sel = (WMSDescriptor)bms.getSelectedItem();
		Assert.assertEquals("Prueba 3", sel.getDescription());
		Assert.assertEquals("Url 3", sel.getUrl());
	}
	@Test
	public void comboInitiallyEmptyCanAddItem() {
		System.out.println("TestBaseMapCombo.comboInitiallyEmptyCanAddItem()");
		BaseMapCombo bms = new BaseMapCombo(trackMap);
		Assert.assertNotNull(bms);
		Assert.assertEquals(0, bms.getItemCount());
		bms.addItem(new WMSDescriptor("Prueba 1", "Url 1",0));
		Assert.assertEquals(1, bms.getItemCount());		
		WMSDescriptor desc = (WMSDescriptor)bms.getSelectedItem();
		Assert.assertEquals("Prueba 1", desc.getDescription());
		Assert.assertEquals("Url 1", desc.getUrl());				
	}
	private WMSDescriptor[] asArray(ArrayList<WMSDescriptor> wmss) {
		WMSDescriptor[] objs = wmss.toArray(new WMSDescriptor[wmss.size()]);
		return objs;
	}
}

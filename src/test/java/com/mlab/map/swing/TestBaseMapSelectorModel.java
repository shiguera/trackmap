package com.mlab.map.swing;

import java.util.ArrayList;

import javax.swing.JComboBox;

import junit.framework.Assert;

import org.junit.Test;

public class TestBaseMapSelectorModel {

	@Test
	public void comboIsEmptyAfterCreation() {
		System.out.println("TestBaseMapSelectorModel.comboIsEmptyAfterCreation()");
		JComboBox bms = new JComboBox(new BaseMapSelectorModel());
		Assert.assertNotNull(bms);
		Assert.assertEquals(0, bms.getItemCount());
	}
	@Test
	public void comboHasElementsIfConstructorWithArrayIsUsed() {
		System.out.println("TestBaseMapSelectorModel.comboHasElementsIfConstructorWithArrayIsUsed()");
		ArrayList<WMSDescriptor> wms = new ArrayList<WMSDescriptor>();
		wms.add(new WMSDescriptor("Prueba 1", "Url 1"));
		wms.add(new WMSDescriptor("Prueba 2", "Url 2"));
				
		WMSDescriptor[] descs = asArray(wms);
		JComboBox bms = new JComboBox(new BaseMapSelectorModel(descs));
		Assert.assertNotNull(bms);
		Assert.assertEquals(2, bms.getItemCount());
		bms.addItem(new WMSDescriptor("Prueba 3", "Url 3"));
		Assert.assertEquals(3, bms.getItemCount());
		WMSDescriptor sel = (WMSDescriptor)bms.getSelectedItem();
		Assert.assertEquals("Prueba 1", sel.getDescription());
		Assert.assertEquals("Url 1", sel.getUrl());
		bms.setSelectedIndex(2);
		sel = (WMSDescriptor)bms.getSelectedItem();
		Assert.assertEquals("Prueba 3", sel.getDescription());
		Assert.assertEquals("Url 3", sel.getUrl());
	}
	@Test
	public void comboInitiallyEmptyCanAddItem() {
		System.out.println("TestBaseMapSelectorModel.comboInitiallyEmptyCanAddItem()");
		JComboBox bms = new JComboBox(new BaseMapSelectorModel());
		Assert.assertNotNull(bms);
		Assert.assertEquals(0, bms.getItemCount());
		bms.addItem(new WMSDescriptor("Prueba 1", "Url 1"));
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

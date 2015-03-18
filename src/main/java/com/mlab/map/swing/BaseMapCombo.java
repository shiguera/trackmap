package com.mlab.map.swing;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import com.mlab.map.TrackMap;
import com.mlab.map.factory.WMSDescriptor;

public class BaseMapCombo extends JComboBox {
	
	TrackMap map;
	public BaseMapCombo(TrackMap map) {
		super(new BaseMapSelectorModel());
		this.map = map;
	}
	public void setDefaultMaps() {
		WMSDescriptor d1 = new WMSDescriptor("IGNBase", "http://www.ign.es/wms-inspire/ign-base?SERVICE=WMS&", 24);
		this.addItem(d1);
		WMSDescriptor d2 = new WMSDescriptor("PNOA", "http://www.ign.es/wms-inspire/pnoa-ma?SERVICE=WMS&", 1);
		this.addItem(d2);
		
	}
	public BaseMapCombo(TrackMap map, WMSDescriptor[] descs) {
		super(new BaseMapSelectorModel(descs));
		this.map = map;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed()");
		if(map != null) {
			map.setBaseLayer(null);
		}
	}
}

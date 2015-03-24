package com.mlab.map.swing;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import org.geotools.map.WMSLayer;

import com.mlab.map.TrackMap;
import com.mlab.map.factory.WMSDescriptor;
import com.mlab.map.factory.WMSFactory;

public class BaseMapCombo extends JComboBox {
	
	TrackMap trackMap;
	public BaseMapCombo(TrackMap map) {
		super(new BaseMapSelectorModel());
		this.trackMap = map;
		init();
		
	}
	public BaseMapCombo(TrackMap map, WMSDescriptor[] descs) {
		super(new BaseMapSelectorModel(descs));
		this.trackMap = map;
		init();
	}
	private void init() {
		this.addActionListener(this);
		//this.setPreferredSize(new Dimension(100,18));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed()");
		WMSDescriptor d = (WMSDescriptor)getSelectedItem();
		WMSLayer layer = WMSFactory.getWMSLayer(d);
		if(trackMap != null && layer != null) {
			trackMap.setBaseLayer(layer);
		}
	}
	public void setDefaultMaps() {
		WMSDescriptor d1 = new WMSDescriptor("IGNBase", "http://www.ign.es/wms-inspire/ign-base?SERVICE=WMS&", 24);
		this.addItem(d1);
		WMSDescriptor d2 = new WMSDescriptor("PNOA", "http://www.ign.es/wms-inspire/pnoa-ma?SERVICE=WMS&", 1);
		this.addItem(d2);
		
	}
}

package com.mlab.map.swing;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import com.mlab.map.TrackMap;

public class BaseMapCombo extends JComboBox {
	
	TrackMap map;
	public BaseMapCombo(TrackMap map) {
		super(new BaseMapSelectorModel());
		this.map = map;
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

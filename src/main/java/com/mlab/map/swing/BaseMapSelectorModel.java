package com.mlab.map.swing;

import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;

import com.mlab.map.factory.WMSDescriptor;

public class BaseMapSelectorModel extends DefaultComboBoxModel  implements MutableComboBoxModel {

	
	public BaseMapSelectorModel() {
		super(new WMSDescriptor[]{});
	}
	
	public BaseMapSelectorModel(WMSDescriptor[] descriptors) {
		super(descriptors);
	}
	
	@Override
	public String toString() {
		return ((WMSDescriptor)getSelectedItem()).getDescription();
	}
}

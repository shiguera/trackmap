package com.mlab.map.swing;

import java.awt.Component;

import javax.swing.JButton;

import org.geotools.swing.JMapPane;

import com.mlab.map.TrackMap;

public interface MapToolBar {

	Component getComponent();
	void setDefaultButtons(TrackMap map, JMapPane mappane);
	void addButton(JButton button);
	int getButtonsCount();
	JButton getButton(int index);
	void addComponent(Component component);
}

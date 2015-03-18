package com.mlab.map.swing;

import java.awt.Component;

import javax.swing.JButton;

import org.geotools.swing.JMapPane;

public interface MapToolBar {

	Component getComponent();
	void setDefaultButtons(JMapPane mappane);
	void addButton(JButton button);
	int getButtonsCount();
	JButton getButton(int index);
	void addComponent(Component component);
}

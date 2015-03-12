package com.mlab.map.swing;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.geotools.swing.JMapPane;

public interface MapView {
	
	JPanel getMainPanel();
	
	JPanel getMapPanel();
	void setMapPanelSize(int width, int height);
	
	JMapPane getJMapPane();
	
	void refreshMap();
	
	void addButton(JButton btn);
	
	MapToolBar getMapToolBar();
	void setMapToolBar(MapToolBar toolbar);
	
	
	// StatusBar
	void dispMessage(String message);
	
	boolean isRendering();
	void setIsRendering(boolean isrndering);
	
	
}

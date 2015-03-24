package com.mlab.map.swing;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.JMapPane;

import com.mlab.patterns.Observer;

public interface MapView extends Observer {
	
	JPanel getMainPanel();
	
	JPanel getMapPanel();
	void setMapPanelSize(int width, int height);
	
	JMapPane getJMapPane();
	void setDisplayArea(ReferencedEnvelope env);
	void refreshMap();
	
	void addButton(JButton btn);
	
	MapToolBar getMapToolBar();
	void setMapToolBar(MapToolBar toolbar);
	
	
	// StatusBar
	void dispMessage(String message);
	
	boolean isRendering();
	void setIsRendering(boolean isrndering);
	
	
}

package com.mlab.map;

import javax.swing.JPanel;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;

public interface MapView {

	// MapPanelSize
	public abstract void setMapPanelSize(int width, int height);
	public abstract void setDisplayArea(ReferencedEnvelope env);

	// MapContent
	public abstract void setMapContent(MapContent mapcontent);

	// Refresh map
	public abstract void refreshMap();

	// Panel Getters	
	public abstract JPanel getMainPanel();
	public abstract JPanel getMapPanel();
	public abstract JMapPane getJMapPane();

	// Status
	public abstract boolean isRendering();

}
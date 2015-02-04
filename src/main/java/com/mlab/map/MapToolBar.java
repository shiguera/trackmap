package com.mlab.map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;

public class MapToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;

	protected MapView mapView;
	
	JButton zoomOutButton, zoomInButton, panButton;

	public MapToolBar(MapView mapview) {
		super();
		setFloatable(false);
		setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		
		this.mapView = mapview;
		
		createBasicButtons();
	}

	private void createBasicButtons() {
		createZoomInButton();
		createZoomOutButton();
		createPanButton();
		
	}
	private void createZoomInButton() {
		zoomInButton = new JButton(new ZoomInAction(mapView.getJMapPane()));
		add(zoomInButton);			
	}
	private void createZoomOutButton() {
		zoomOutButton = new JButton(new ZoomOutAction(mapView.getJMapPane()));
		add(zoomOutButton);			
	}
	private void createPanButton() {
		panButton = new JButton(new PanAction(mapView.getJMapPane()));
		add(panButton);	
	}
	
}

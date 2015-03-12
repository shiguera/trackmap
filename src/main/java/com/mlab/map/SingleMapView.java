package com.mlab.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;

public class SingleMapView implements MapView {
	private final int DEFAULT_MAPPANE_WIDTH = 600;
	private final int DEFAULT_MAPPANE_HEIGHT = 400;	
	private final int BTN_WIDTH = 24;
	private final int BTN_HEIGHT = 24;
	
	private JPanel mainPanel;
	private JMapPane jmapPane;
	private MapToolBar toolBar;
	
	private JPanel statusBar;
	private JLabel lblMsg;
	
	private boolean isRendering;
	
	public SingleMapView(MapContent mapcontent) {
		jmapPane = new JMapPane(mapcontent);
		createLayout();
	}
	private void createLayout() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		toolBar = null;
		
		jmapPane.setPreferredSize(new Dimension(DEFAULT_MAPPANE_WIDTH, DEFAULT_MAPPANE_HEIGHT));
		mainPanel.add(jmapPane, BorderLayout.CENTER);

		statusBar = new JPanel();
		statusBar.setBackground(Color.GREEN);
		statusBar.setBorder(BorderFactory.createEtchedBorder());
		lblMsg = new JLabel("Test");
		lblMsg.setBackground(Color.LIGHT_GRAY);
		statusBar.add(lblMsg);
		mainPanel.add(statusBar, BorderLayout.SOUTH);
		
	}
	
	
	@Override
	public JPanel getMainPanel() {
		return mainPanel;
	}
	@Override
	public JPanel getMapPanel() {
		return jmapPane;
	}
	@Override
	public void addButton(JButton btn) {
		if (toolBar != null) {
			toolBar.addButton(btn);			
		}
	}
	@Override
	public void dispMessage(String message) {
		lblMsg.setText(message);
	}
	@Override
	public boolean isRendering() {
		return isRendering;
	}
	@Override
	public void setIsRendering(boolean isrndering) {
		isRendering = isrndering;
		
	}
	
	@Override
	public void setMapToolBar(MapToolBar toolbar) {
		this.toolBar = toolbar;
		mainPanel.add(toolBar.getComponent(), BorderLayout.NORTH);
	}
	@Override
	public JMapPane getJMapPane() {
		return this.jmapPane;
	}
	@Override
	public MapToolBar getMapToolBar() {
		return toolBar;
	}
	@Override
	public void setMapPanelSize(int width, int height) {
		this.jmapPane.setPreferredSize(new Dimension(width, height));
	}
	@Override
	public void refreshMap() {
		jmapPane.setDisplayArea(jmapPane.getDisplayArea());		
	}

}

package com.mlab.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapPaneEvent;
import org.geotools.swing.event.MapPaneListener;
/**
 * 
 * @author shiguera
 *
 */
public class MapViewImpl implements MapPaneListener, MapView {

    private final Logger LOG = Logger.getLogger(getClass().getName());

    protected MapToolBar toolBar;
	protected JPanel mainPanel, mapPanel;
	protected JMapPane jmapPane;
	protected JButton buttonZoomIn, buttonZoomOut, buttonPan, buttonZoomSpain, buttonZoomTrack,
		buttonSyncVideo;
	//protected StatusBarLabel statusBarLabelRendering;
	protected boolean isRendering;
	
	// Constructor
	public MapViewImpl() {
				
		initJMapPane();
		
		initMapPanel();
		
//		initButtonsPanel();
		//statusBarLabelRendering = new StatusBarLabelRendering(jmapPane);
//		buttonsPanel.add(statusBarLabelRendering.getPanel());
		
		initToolBar();
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(mapPanel, BorderLayout.CENTER);
		mainPanel.add(toolBar, BorderLayout.NORTH);

	}
	private void initJMapPane() {
		jmapPane = new JMapPane();
		jmapPane.setRenderer(new StreamingRenderer());
		jmapPane.addMapPaneListener(this);
	}
	private void initMapPanel() {
		mapPanel = new JPanel(new BorderLayout());
		//jmapPane.setBackground(Color.GREEN);
		mapPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		mapPanel.setBackground(Color.WHITE);		
		mapPanel.add(jmapPane, BorderLayout.CENTER);		
	}
	private void initToolBar() {
		toolBar = new MapToolBar(this);
	}
	
	
	@Override
	public void setMapPanelSize(int width, int height) {
		this.jmapPane.setPreferredSize(new Dimension(width, height));
	}
	@Override
	public void setMapContent(MapContent mapcontent) {
		jmapPane.setMapContent(mapcontent);		
		jmapPane.setDisplayArea(mapcontent.getMaxBounds());
	}
	// Refresh map
	@Override
	public void refreshMap() {
		jmapPane.setDisplayArea(jmapPane.getDisplayArea());
	}

	// Getters	
	@Override
	public JPanel getMainPanel() {
		return mainPanel;
	}
	@Override
	public JPanel getMapPanel() {
		return mapPanel;
	}
	@Override
	public JMapPane getJMapPane() {
		return jmapPane;
	}
	@Override
	public boolean isRendering() {
		return isRendering;
	}
	@Override
	public void setDisplayArea(ReferencedEnvelope env) {
		this.jmapPane.setDisplayArea(env);
	}
	
	// Interface MapPaneListener
	/* (non-Javadoc)
	 * @see com.mlab.roadplayer.map.MapView#onNewMapContent(org.geotools.swing.event.MapPaneEvent)
	 */
	@Override
	public void onNewMapContent(MapPaneEvent ev) {
		// TODO Auto-generated method stub		
	}
	/* (non-Javadoc)
	 * @see com.mlab.roadplayer.map.MapView#onDisplayAreaChanged(org.geotools.swing.event.MapPaneEvent)
	 */
	@Override
	public void onDisplayAreaChanged(MapPaneEvent ev) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see com.mlab.roadplayer.map.MapView#onRenderingStarted(org.geotools.swing.event.MapPaneEvent)
	 */
	@Override
	public void onRenderingStarted(MapPaneEvent ev) {
		isRendering=true;		
	}
	/* (non-Javadoc)
	 * @see com.mlab.roadplayer.map.MapView#onRenderingStopped(org.geotools.swing.event.MapPaneEvent)
	 */
	@Override
	public void onRenderingStopped(MapPaneEvent ev) {
		isRendering=false;		
	}
	
}

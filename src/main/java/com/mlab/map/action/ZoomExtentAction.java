package com.mlab.map.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import com.mlab.map.TrackMap;


public class ZoomExtentAction extends MapAction {
	private final Logger LOG = Logger.getLogger(ZoomExtentAction.class);
	
	final static String TEXT = "";
	final static String ACTION_COMMAND = "ZOOM_EXTENT";
	final static String ICON_NAME = "spain.png";
	final static String DESCRIPTION = "Zoom extent";
	
	TrackMap controller;
	
	public ZoomExtentAction(TrackMap controller) {
		super(controller, TEXT, ACTION_COMMAND, ICON_NAME, DESCRIPTION);
		this.controller = controller;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		LOG.debug("ZoomExtentAction.actionPerformed()");
		controller.zoomExtent();
	}

}

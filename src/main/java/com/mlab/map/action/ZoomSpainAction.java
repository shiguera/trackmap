package com.mlab.map.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import com.mlab.map.TrackMap;


public class ZoomSpainAction extends MapAction {
	private final Logger LOG = Logger.getLogger(ZoomSpainAction.class);
	
	final static String TEXT = "";
	final static String ACTION_COMMAND = "ZOOM_SPAIN";
	final static String ICON_NAME = "spain.png";
	final static String DESCRIPTION = "Zoom spain";
	
	TrackMap trackMap;
	
	public ZoomSpainAction(TrackMap controller) {
		super(controller, TEXT, ACTION_COMMAND, ICON_NAME, DESCRIPTION);
		this.trackMap = controller;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		LOG.debug("ZoomSpainAction.actionPerformed()");
		trackMap.zoomSpain();
	}

}

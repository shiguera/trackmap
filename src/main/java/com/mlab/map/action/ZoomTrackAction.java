package com.mlab.map.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import com.mlab.map.TrackMap;


public class ZoomTrackAction extends MapAction {
	private final Logger LOG = Logger.getLogger(ZoomTrackAction.class);
	
	final static String TEXT = "";
	final static String ACTION_COMMAND = "ZOOM_TRACK";
	final static String ICON_NAME = "track.png";
	final static String DESCRIPTION = "Zoom track";
	
	TrackMap controller;
	
	public ZoomTrackAction(TrackMap controller) {
		super(controller, TEXT, ACTION_COMMAND, ICON_NAME, DESCRIPTION);
		this.controller = controller;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		LOG.debug("ZoomExtentAction.actionPerformed()");
		controller.zoomTrack();
	}

}

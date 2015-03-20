package com.mlab.map.swing;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;

import org.geotools.swing.JMapPane;

import com.mlab.map.TrackMapModel;

public class StatusBar extends JPanel {
	
	TrackMapModel model;
	JMapPane mapPane;
	
	StatusBarLabelSRS srsLabel;
	StatusBarLabelRendering renderingLabel;
	
	public StatusBar(TrackMapModel model, JMapPane mappane) {
		this.model = model;
		this.mapPane = mappane;
		init();
	}
	private void init() {
		setBackground(new Color(143,188,143));
		setBorder(BorderFactory.createEtchedBorder());
		
		renderingLabel = new StatusBarLabelRendering(mapPane);
		this.add(renderingLabel);

		this.add(Box.createHorizontalStrut(10));
		
		srsLabel = new StatusBarLabelSRS(model);
		this.add(srsLabel);
		
		
		
	}
	
}

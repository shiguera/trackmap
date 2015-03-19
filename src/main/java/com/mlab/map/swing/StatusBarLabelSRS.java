package com.mlab.map.swing;

import java.awt.Image;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.mlab.map.TrackMapModel;
import com.mlab.patterns.Observable;
import com.mlab.patterns.Observer;

public class StatusBarLabelSRS extends StatusBarLabel implements Observer {

	TrackMapModel model;
	
	
	public StatusBarLabelSRS(TrackMapModel model) {
		super("SRS:", createImageIcon("globe_32.png",22,22));
		this.model = model;
		update();
	}
	@Override
	protected void setLayout() {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setVerticalAlignment(CENTER);
	}

	@Override
	public Observable getObservable() {
		return model;
	}

	@Override
	public void update() {
		if(model != null && model.getCoordinateReferenceSystem() != null) {
			setText(model.getCoordinateReferenceSystem().getName().toString());			
		} else {
			setText("SRS: null");
		}		
	}


}

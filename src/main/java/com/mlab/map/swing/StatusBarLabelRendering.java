package com.mlab.map.swing;

import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;


public class StatusBarLabelRendering extends StatusBarLabel {

	JMapPane mapPane;
	
	public StatusBarLabelRendering(JMapPane mappane) {
		super("", createImageIcon("idle.gif",22,22));
		this.mapPane = mappane;
		mapPane.addMapPaneListener(new MapPaneAdapter() {
            @Override
            public void onRenderingStarted(MapPaneEvent ev) {
                setRendering(true);
            }

            @Override
            public void onRenderingStopped(MapPaneEvent ev) {
                setRendering(false);
            }
        });

	}
	private void setRendering(boolean rendering) {
		String iconpath = (rendering?"busy.gif":"idle.gif");
		setIcon(createImageIcon(iconpath, 16, 16));
	}

	@Override
	protected void setLayout() {
		
	}

}

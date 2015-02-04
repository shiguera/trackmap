package com.mlab.map;


import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.geotools.map.Layer;
import org.geotools.map.MapViewport;

import com.mlab.map.factory.GeoToolsMapFactory;
import com.mlab.map.layer.ShpLayer;

public class TestTrackMapView {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TestTrackMapView();
			}
		});
	}
	
	TrackMap controller;
	
	public TestTrackMapView() {
		PropertyConfigurator.configure("log4j.properties");
		
		JFrame frame = new JFrame("Pruebas");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		TrackMapModel mapModel = new TrackMapModel();

		MapView view = new MapViewImpl();
		view.setMapPanelSize(600, 400);
		
		controller = new TrackMap(mapModel);
		controller.setMapView(view);
		
		addLayers();
		
		frame.getContentPane().add(view.getMainPanel());		
		frame.setLocation(200,200);
		frame.pack();
		frame.setVisible(true);
			
	}
	private void addLayers() {
//		controller.setViewPort(new MapViewport(MapFactory.getWorldEpsg3857Bounds()));
//		Layer layer3 = WMSFactory.getProxyLayer(1);
//		System.out.println("WMS: " + layer3.getBounds().getCoordinateReferenceSystem().getName());
//		boolean result = controller.addLayer(layer3);
		
		ShpLayer layer = loadDistritos();
		//System.out.println(layer.getBounds().getCoordinateReferenceSystem().getName() + " - " + layer.getBounds());
		
		controller.addVectorLayer(layer);
		
		ShpLayer layer2 = loadDistritos_25830();
		//System.out.println(layer2.getBounds().getCoordinateReferenceSystem().getName() + " - " + layer2.getBounds());
		controller.addVectorLayer(layer2);

		controller.setViewPort(new MapViewport(layer2.getLayer().getBounds()));

	}
	private ShpLayer loadDistritos() {
		File file = new File(ClassLoader.getSystemResource("Distrtitos_4326.shp").getFile());

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer layer = GeoToolsMapFactory.createShpLayer(file);
		Assert.assertNotNull(layer);
		Assert.assertNotNull(layer.getLayer());
		return layer;
	}
	private ShpLayer loadDistritos_25830() {
		File file = new File(
				ClassLoader.getSystemResource("Distrtitos_25830.shp").getFile());

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer layer = GeoToolsMapFactory.createShpLayer(file);
		Assert.assertNotNull(layer);
		Assert.assertNotNull(layer.getLayer());
		return layer;
	}

	private Layer loadED50Layer() {
		File file = new File(
				ClassLoader.getSystemResource("CallejeroPol.shp").getFile());

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer layer = GeoToolsMapFactory.createShpLayer(file);
		Assert.assertNotNull(layer);
		Assert.assertNotNull(layer.getLayer());
		return layer.getLayer();
	}
	private Layer loadETRS89Layer() {
		File file = new File(
				ClassLoader.getSystemResource("poligonos_ccaa_etrs89.shp").getFile());

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer layer = GeoToolsMapFactory.createShpLayer(file);
		Assert.assertNotNull(layer);
		Assert.assertNotNull(layer.getLayer());
		return layer.getLayer();
	}

}

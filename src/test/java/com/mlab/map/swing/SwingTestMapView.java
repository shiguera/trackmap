package com.mlab.map.swing;


import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapViewport;
import org.geotools.styling.Style;

import com.mlab.map.TrackMap;
import com.mlab.map.TrackMapModel;
import com.mlab.map.factory.GeoToolsFactory;
import com.mlab.map.factory.StyleFac;
import com.mlab.map.layer.ShpLayer;
import com.mlab.map.swing.MapToolBar;
import com.mlab.map.swing.MapToolBarImpl;
import com.mlab.map.swing.MapView;
import com.mlab.map.swing.SingleMapView;

public class SwingTestMapView {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SwingTestMapView();
			}
		});
	}
	
	TrackMap trackMap;
	ShpLayer layerEsri, layerWithoutcrs, layer4326;
	
	public SwingTestMapView() {
		PropertyConfigurator.configure("log4j.properties");
		
		JFrame frame = new JFrame("SwingTestMapView");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		TrackMapModel mapModel = new TrackMapModel();
		trackMap = new TrackMap(mapModel);

		MapView view = new SingleMapView(mapModel.getMapContent());
		
		MapToolBar toolbar = new MapToolBarImpl();
		toolbar.setDefaultButtons(view.getJMapPane());
		view.setMapToolBar(toolbar);
		
		addVectorLayerEsriCode();
		addVectorLayerWithoutPrjFile();
		addVectorLayer4326();
		

		ReferencedEnvelope env = new ReferencedEnvelope(
				mapModel.getLayer(1).getBounds(),
				mapModel.getCoordinateReferenceSystem());
		mapModel.setViewPort(new MapViewport(env));

//		CoordinateReferenceSystem crs=null;
//		try {
//			crs = CRS.parseWKT("GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.017453292519943295]]");
//		} catch (FactoryException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Assert.assertNotNull(crs);
//		System.out.println(crs.getName().toString());
		
		
		frame.getContentPane().add(view.getMainPanel());		
		frame.setLocation(200,200);
		frame.pack();
		frame.setVisible(true);
			
	}
	private void addVectorLayerEsriCode() {
		File file = new File(ClassLoader.getSystemResource("Distrtitos_4326_ESRICODE.shp").getFile());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());

		Style style = StyleFac.createLineStyle(Color.RED, 8);
		layerEsri = GeoToolsFactory.createShpLayer(file, style);
		Assert.assertNotNull(layerEsri);
		Assert.assertNotNull(layerEsri.getLayer());

		System.out.println(layerEsri.getLayer().getBounds().getCoordinateReferenceSystem().getName().toString());
		trackMap.addVectorLayer(layerEsri);
		Assert.assertEquals(1, trackMap.getMapModel().getLayerCount());
	}
	private void addVectorLayerWithoutPrjFile() {
		File file = new File(ClassLoader.getSystemResource("Distrtitos_4326.shp").getFile());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());

		Style style = StyleFac.createLineStyle(Color.YELLOW, 4);
		layerWithoutcrs = GeoToolsFactory.createShpLayer(file, style);
		Assert.assertNotNull(layerWithoutcrs);
		Assert.assertNotNull(layerWithoutcrs.getLayer());
		System.out.println(layerWithoutcrs.getLayer());
		//Assert.assertNotNull(layer.getLayer().getBounds().getCoordinateReferenceSystem());
		trackMap.addVectorLayer(layerWithoutcrs);
		Assert.assertEquals(2, trackMap.getMapModel().getLayerCount());
	}
	private void addVectorLayer4326() {
		File file = new File(ClassLoader.getSystemResource("Distrtitos_4326.shp").getFile());
		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		layer4326 = GeoToolsFactory.createShpLayer(file);
		Assert.assertNotNull(layer4326);
		Assert.assertNotNull(layer4326.getLayer());
		Assert.assertEquals("WGS 84", layer4326.getLayer().getBounds().getCoordinateReferenceSystem().getName().getCode());
		trackMap.addVectorLayer(layer4326);
	}
	
	private void addLayers() {
//		trackMap.setViewPort(new MapViewport(MapFactory.getWorldEpsg3857Bounds()));
//		Layer layer3 = WMSFactory.getProxyLayer(1);
//		System.out.println("WMS: " + layer3.getBounds().getCoordinateReferenceSystem().getName());
//		boolean result = trackMap.addLayer(layer3);
		
		//System.out.println(layer.getBounds().getCoordinateReferenceSystem().getName() + " - " + layer.getBounds());
		
//		
//		ShpLayer layer2 = loadDistritos_25830();
//		//System.out.println(layer2.getBounds().getCoordinateReferenceSystem().getName() + " - " + layer2.getBounds());
//		trackMap.addVectorLayer(layer2);
//
//		trackMap.setViewPort(new MapViewport(layer2.getLayer().getBounds()));

	}
	private ShpLayer loadDistritos_25830() {
		File file = new File(
				ClassLoader.getSystemResource("Distrtitos_25830.shp").getFile());

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer layer = GeoToolsFactory.createShpLayer(file);
		Assert.assertNotNull(layer);
		Assert.assertNotNull(layer.getLayer());
		return layer;
	}

	private Layer loadED50Layer() {
		File file = new File(
				ClassLoader.getSystemResource("CallejeroPol.shp").getFile());

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer layer = GeoToolsFactory.createShpLayer(file);
		Assert.assertNotNull(layer);
		Assert.assertNotNull(layer.getLayer());
		return layer.getLayer();
	}
	private Layer loadETRS89Layer() {
		File file = new File(
				ClassLoader.getSystemResource("poligonos_ccaa_etrs89.shp").getFile());

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());
		
		ShpLayer layer = GeoToolsFactory.createShpLayer(file);
		Assert.assertNotNull(layer);
		Assert.assertNotNull(layer.getLayer());
		return layer.getLayer();
	}

}

package com.mlab.map.layer;

import java.awt.Color;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.StyleLayer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.mlab.map.factory.GeoToolsFactory;
import com.mlab.map.factory.StyleFac;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class MobileLayer extends AbstractVectorLayer {

	private final Color DEFAULT_COLOR = Color.ORANGE;
	private final int DEFAULT_WIDTH = 18;
	private final String DEFAULT_NAME = "ONEPOINT";
	
	Layer layer;
	SimpleFeature feature;
	double x;
	double y;
	Style style;
	/**
	 * Constructor para capa en un SRS determinado
	 * @param name Nombre de la capa
	 * @param x Coordenada x inicial del punto de la capa
	 * @param y Coordenada y inicial del punto de la capa
	 */	
	public MobileLayer(String name, CoordinateReferenceSystem crs, double x, double y) {
		//System.out.println("OnePointLayer.OnePointLayer()");
		this.name = name;
		this.x = x;
		this.y = y;
		
		Point point = GeoToolsFactory.getGeometryFactory().createPoint(new Coordinate(x,y));
		style = createDefaultStyle();
		
		final SimpleFeatureType TYPE = GeoToolsFactory.createPointFeatureType(crs);		
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		featureBuilder.add(point);
		featureBuilder.add(this.name);
		feature = featureBuilder.buildFeature(null);

		DefaultFeatureCollection collection = new DefaultFeatureCollection();
		collection.add(feature);	
				
		layer = null;
		if(collection.size()>0) {
			layer = new FeatureLayer(collection, style);	
			layer.setTitle(name);
		}
	}
	/**
	 * Constructor para capa en el SRS 4326: geográficas WGS84
	 * @param name Nombre de la capa
	 * @param x Coordenada x inicial del punto de la capa
	 * @param y Coordenada y inicial del punto de la capa
	 */
	public MobileLayer(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
		
		Point point = GeoToolsFactory.getGeometryFactory().createPoint(new Coordinate(x,y));
		style = createDefaultStyle();
		
		final SimpleFeatureType TYPE = GeoToolsFactory.createWGS84PointFeatureType();		
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		featureBuilder.add(point);
		featureBuilder.add(name);
		feature = featureBuilder.buildFeature(null);

		DefaultFeatureCollection collection = new DefaultFeatureCollection();
		collection.add(feature);	
		
		layer = null;
		if(collection.size()>0) {
			layer = new FeatureLayer(collection, style);	
			layer.setTitle(name);
		}
	}

	private Style createDefaultStyle() {
		return StyleFac.createPointStyle(DEFAULT_COLOR, DEFAULT_WIDTH);
	}
	public void move(double newx, double newy) {
		FeatureLayer fl = (FeatureLayer) layer;
		try {
			feature = fl.getSimpleFeatureSource().getFeatures()
					.features().next();
			Point point = GeoToolsFactory.getGeometryFactory().createPoint(new Coordinate(newx, newy));
			feature.setDefaultGeometry(point);
			this.x = newx;
			this.y = newy;
		} catch (Exception e1) {
			System.out.println("OnePointLayer.move() ERROR:" + e1.getMessage());
		}
	}
	/**
	 * Devuelve la Layer de geotools asociada
	 * Se trata de una org.geotools.map.FeatureLayer
	 */
	@Override
	public Layer getLayer() {
		return layer;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public Style getStyle() {
		return style;
	}
	public void setStyle(Style style) {
		this.style = style;
		((StyleLayer)layer).setStyle(this.style);
	}

	
	@Override
	protected String getXmlContent() {
		return "";
	}
	@Override
	public boolean fromXml() {
		// TODO Auto-generated method stub
		return false;
	}


}

package com.mlab.map.layer;

import java.awt.Color;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;

import com.mlab.map.factory.GeoToolsMapFactory;
import com.mlab.map.factory.StyleFac;
import com.vividsolutions.jts.geom.LineString;

public class LineStringLayer extends AbstractVectorLayer {

	Layer layer;
	Style style;
	boolean isPointLayer;
	
	public LineStringLayer(LineString ls, boolean pointlayer) {
		SimpleFeatureCollection coll=null;
		isPointLayer = pointlayer;
		if(pointlayer) {
			coll = GeoToolsMapFactory.lineStringToPointFeatureCollection(ls);
			this.style = StyleFac.createPointStyle(Color.BLUE, 2);
		} else {
			coll = GeoToolsMapFactory.lineStringToLineFeatureCollection(ls);
			this.style = StyleFac.createLineStyle(Color.BLUE, 2);
		}
		this.layer = new FeatureLayer(coll, style);
	}
	
	@Override
	public boolean fromXml() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Style getStyle() {
		return style;
	}

	@Override
	public Layer getLayer() {
		return layer;
	}

	@Override
	protected String getXmlContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPointLayer() {
		return isPointLayer;
	}

}

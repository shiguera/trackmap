package com.mlab.map.layer;

import java.io.File;

import org.geotools.map.Layer;

import com.mlab.map.factory.GeoToolsFactory;

public class TifLayer extends AbstractRasterLayer {

	
	Layer layer;
	
	public TifLayer(File file) {
		this.url = file.getPath();
		this.layer = GeoToolsFactory.readRasterLayer(file);
	}

	public TifLayer(File file, boolean colorbands) {
		if(colorbands) {
			this.layer = GeoToolsFactory.readRasterColorLayer(file);
		} else {
			this.layer = GeoToolsFactory.readRasterLayer(file);
		}	
	}
	@Override
	public Layer getLayer() {
		return layer;
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

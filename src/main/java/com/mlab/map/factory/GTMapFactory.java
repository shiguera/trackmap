package com.mlab.map.factory;

import java.io.File;

import org.apache.log4j.Logger;

import com.mlab.map.layer.ShpLayer;
import com.mlab.map.layer.TifLayer;

public class GTMapFactory implements MapFactory {
	private final Logger LOG = Logger.getLogger(GTMapFactory.class);
	
	@Override
	public ShpLayer createShpLayer(File file) {
		if (file == null || !file.exists()) {
			LOG.warn("GTMapFactory.createShpLayer() ERROR: File doesn't exist ");
			return null;
		}
		ShpLayer layer = new ShpLayer();
		layer.setLayer(file);
		return layer;
	}

	@Override
	public TifLayer createTifLayer(File tiffile) {
		// TODO Auto-generated method stub
		return null;
	}

}

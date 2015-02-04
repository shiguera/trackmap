package com.mlab.map.factory;

import java.io.File;

import com.mlab.map.layer.ShpLayer;
import com.mlab.map.layer.TifLayer;

public interface MapFactory {
	
	ShpLayer createShpLayer(File shpfile);
	TifLayer createTifLayer(File tiffile);
	
}

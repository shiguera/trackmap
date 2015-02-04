package com.mlab.map.layer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;

import com.mlab.map.factory.StyleFac;
/**
 * Encapsula una FeatureLayer de geotools más un estilo style
 * 
 * @author shiguera
 *
 */
public class ShpLayer extends AbstractVectorLayer {

	private final Logger LOG = Logger.getLogger(ShpLayer.class);
	
	protected final Color DEFAULT_COLOR = Color.BLACK;
	protected final int DEFAULT_WIDTH = 1;

	Layer layer;
	Style style;
	
	// Constructores
	public ShpLayer() {
		//System.out.println("ShpLayer.ShpLayer()");
		super();
		layer = null;
		style = null;
	}
	public ShpLayer(File shpfile) {
		//LOG.debug("ShpLayer.ShpLayer()");
		layer = null;
		setLayer(shpfile);
	}
	public ShpLayer(FeatureLayer layer) {
		style = StyleFac.createStyle(layer.getFeatureSource(), DEFAULT_COLOR, DEFAULT_WIDTH);
		this.layer = layer;
		setName(layer.getTitle());
	}
	
	public void setLayer(File shpfile) {
		//LOG.debug("ShpLayer.setLayer(): "+shpfile.getPath());
		layer = null;
		FileDataStore store;
		this.url = shpfile.getPath();
		try {
			store = FileDataStoreFinder.getDataStore(shpfile);
			SimpleFeatureSource featureSource = store.getFeatureSource();

			// El método en destino comprueba la geometría de la capa
			style = StyleFac.createStyle(featureSource, DEFAULT_COLOR, DEFAULT_WIDTH);
			layer = new FeatureLayer(featureSource, style);		
			
			setName(shpfile.getName());
			setUrl(shpfile.getPath());
			
			
			//System.out.println(layer.getBounds());
		} catch (IOException e) {
			LOG.warn("setLayer(): Can't create ShpLayer "+e.getMessage());
		}					
	}
	public void setLayer(File shpfile, Style sstyle) {
		//LOG.debug("ShpLayer.setLayer(): "+shpfile.getPath()+", "+sstyle.getName());
		//LOG.debug("ShpLayer.setLayer() file exists:"+shpfile.exists());
		layer = null;
		FileDataStore store;
		this.url = shpfile.getPath();
		try {
			store = FileDataStoreFinder.getDataStore(shpfile);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			layer = new FeatureLayer(featureSource, sstyle);		
			setName(shpfile.getName());
			setUrl(shpfile.getPath());
			
			//LOG.debug(layer.getBounds());
		} catch (IOException e) {
			LOG.warn("setLayer(): Can't create ShpLayer "+e.getMessage());
		}					
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		//LOG.debug("setName(): " + name);
		if(this.layer != null) {
			layer.setTitle(name);
		}
	}
	@Override
	public Layer getLayer() {
		return layer;
	}
	@Override
	public Style getStyle() {
		return style;
	}

	// Interface XmlSerializable
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

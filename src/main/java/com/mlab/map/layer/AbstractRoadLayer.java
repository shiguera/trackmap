package com.mlab.map.layer;

import org.apache.log4j.Logger;
import org.geotools.map.Layer;

import com.mlab.gpx.impl.util.XmlFactory;




public abstract class AbstractRoadLayer implements RoadLayer {
	private final Logger LOG = Logger.getLogger(AbstractRoadLayer.class);
	
	protected final String XML_NODE_NAME = "layer";
	protected String name = "";
	protected String url = "";
	
	protected AbstractRoadLayer() {
		this.name = "";
		this.url = "";
	}
	
	@Override
	public abstract Layer getLayer();
	
	@Override 
	public String getXmlNodename() {
		return this.XML_NODE_NAME;
	}
	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
		
	}
	@Override
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		//LOG.debug("setUrl(): " + url);
		this.url = url;
	}
	/**
	 * Proporciona el marcado xml de la capa.
	 * Esta clase proporciona el nodo 'layer' con su nodo 'url'
	 * Cada clase concreta debe proporcionar el resto del marcado en el método 
	 * getXmlContent()
	 */
	@Override
	public String toXml() {
		StringBuilder builder = new StringBuilder();
		builder.append(XmlFactory.createOpenTag(getXmlNodename()));
		builder.append(urlXmlNode());
		builder.append(getXmlContent()); // Factory method
		builder.append(XmlFactory.createCloseTag(getXmlNodename()));
		return builder.toString();
	}
	private String urlXmlNode() {
		StringBuilder builder = new StringBuilder();
		builder.append("<url>");
		builder.append(this.url);
		builder.append("</url>");
		return builder.toString();
	}
	/**
	 * Método abstracto para implementar en cada clase derivada. 
	 * Se utiliza en el método 'toXml()'
	 * @return
	 */
	protected abstract String getXmlContent();

}

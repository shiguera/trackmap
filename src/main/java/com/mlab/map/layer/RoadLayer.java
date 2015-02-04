package com.mlab.map.layer;

import org.geotools.map.Layer;

/**
 * Interface base para las capas visualizables de RoadPlayer<br/>
 * Todas las RoadLayer tienen un Name, una url y un método <em>getLayer()</em>
 * que devuelve la <em>org.geotools.map.Layer</em> asociada. 
 * @author shiguera
 *
 */
public interface RoadLayer extends XmlSerializable {
	public String getName();
	public void setName(String name);
	public String getUrl();	
	public void setUrl(String url);
	public Layer getLayer();
	

}

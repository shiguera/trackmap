package com.mlab.map.layer;

/**
 * Has methods toXml() and fromXml()
 * @author shiguera
 *
 */
public interface XmlSerializable {
	/**
	 * Writes a String with the object as xml node
	 * @return
	 */
	String toXml();
	/**
	 * Read object fields from xml String
	 * @return
	 */
	boolean fromXml();
	/**
	 * Returns the xml node name for this class
	 */
	String getXmlNodename();
	
}

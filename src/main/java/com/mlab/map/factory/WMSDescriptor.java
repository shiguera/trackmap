package com.mlab.map.factory;

public class WMSDescriptor {
	private String description;
	private String url;
	private int layerNum;
	
	public WMSDescriptor(String desc, String url, int layernum) {
		this.description = desc;
		this.url = url;
		this.setLayerNum(layernum);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getLayerNum() {
		return layerNum;
	}

	public void setLayerNum(int layerNum) {
		this.layerNum = layerNum;
	}
	
	@Override
	public String toString() {
		return description;
	}
}

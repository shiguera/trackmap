package com.mlab.map.swing;

public class WMSDescriptor {
	private String description;
	private String url;
	
	public WMSDescriptor(String desc, String url) {
		this.description = desc;
		this.url = url;
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
	

}

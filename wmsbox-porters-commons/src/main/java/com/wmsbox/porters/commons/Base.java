package com.wmsbox.porters.commons;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Base implements Serializable {

	private static final long serialVersionUID = 4784947377698573052L;

	private final long creationTime;
	private final Map<String, Serializable> metainfo;

	public Base() {
		this.creationTime = System.currentTimeMillis();
		this.metainfo = new HashMap<String, Serializable>();
	}

	public long getCreationTime() {
		return this.creationTime;
	}

	public void metaInfo(String key, Serializable value) {
		this.metainfo.put(key, value);
	}
}

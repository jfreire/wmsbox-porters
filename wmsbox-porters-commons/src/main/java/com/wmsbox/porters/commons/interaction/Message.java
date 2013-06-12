package com.wmsbox.porters.commons.interaction;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {

	private static final long serialVersionUID = -4433479638075159900L;

	private final String key;
	private final Serializable[] params;

	public Message(String key, Serializable[] params) {
		this.key = key;
		this.params = params;
	}

	public String getKey() {
		return this.key;
	}

	public Serializable[] getParams() {
		return this.params;
	}
	
	public String toString() {
		return this.key + Arrays.toString(this.params);
	}
}

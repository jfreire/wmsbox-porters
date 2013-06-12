package com.wmsbox.porters.commons.interaction;

public class Button implements Action {

	private static final long serialVersionUID = -6754711386787226293L;

	private final String key;

	public Button(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
	
	public String toString() {
		return "Button." + this.key;
	}
}

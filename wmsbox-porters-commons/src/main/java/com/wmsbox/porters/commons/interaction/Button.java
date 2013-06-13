package com.wmsbox.porters.commons.interaction;

public class Button extends Action {

	private static final long serialVersionUID = -6754711386787226293L;

	public Button(String key, String text) {
		super(key, text);
	}

	@Override
	public String toString() {
		return "Button." + getKey();
	}
}

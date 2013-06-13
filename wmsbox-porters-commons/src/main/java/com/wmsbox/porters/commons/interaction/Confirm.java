package com.wmsbox.porters.commons.interaction;

public class Confirm extends Action {

	private static final long serialVersionUID = -6754711386787226293L;

	public Confirm(String key, String message) {
		super(key, message);
	}

	@Override
	public String toString() {
		return "Confirm." + getKey();
	}
}

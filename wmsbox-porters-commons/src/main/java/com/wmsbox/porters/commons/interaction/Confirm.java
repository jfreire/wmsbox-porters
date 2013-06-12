package com.wmsbox.porters.commons.interaction;

import java.io.Serializable;

public class Confirm extends Message implements Action {

	private static final long serialVersionUID = -6754711386787226293L;

	public Confirm(String key, Serializable[] params) {
		super(key, params);
	}
	
	public String toString() {
		return "Confirm." + super.toString();
	}
}

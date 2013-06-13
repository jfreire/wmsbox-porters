package com.wmsbox.porters.commons;

import java.io.Serializable;
import java.util.Locale;

public class Context implements Serializable {

	private static final long serialVersionUID = -4262900675607428340L;

	private final String porter;
	private final Locale locale;

	public Context(String porter, Locale locale) {
		this.porter = porter;
		this.locale = locale;
	}

	public String getPorter() {
		return this.porter;
	}

	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public String toString() {
		return this.porter + "(" + this.locale.getDisplayName() + ")";
	}
}

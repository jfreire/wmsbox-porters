package com.wmsbox.porters.commons;

import java.io.Serializable;
import java.util.Locale;

public class Context implements Serializable {

	private static final long serialVersionUID = -4262900675607428340L;

	private final String porter;
	private final Locale locale;
	private final String sessionId;

	public Context(String sessionId, String porter, Locale locale) {
		this.porter = porter;
		this.locale = locale;
		this.sessionId = sessionId;
	}

	public String getPorter2() {
		return this.porter;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	public String toString() {
		return this.porter + "(" + this.locale.getDisplayName() + ")";
	}
}

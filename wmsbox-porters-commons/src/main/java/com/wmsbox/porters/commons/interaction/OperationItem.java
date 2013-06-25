package com.wmsbox.porters.commons.interaction;

import java.io.Serializable;

public abstract class OperationItem implements Serializable {

	private static final long serialVersionUID = -3584128677807060571L;

	private final String key;
	private final String text;

	public OperationItem(String key, String text) {
		this.key = key;
		this.text = text;
	}

	public String getKey() {
		return this.key;
	}

	public String getText() {
		return this.text;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.key.equals(((OperationItem) obj).key);
	}

	@Override
	public int hashCode() {
		return this.key.hashCode();
	}
}

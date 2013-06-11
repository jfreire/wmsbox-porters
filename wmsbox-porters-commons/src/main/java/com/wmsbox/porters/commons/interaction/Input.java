package com.wmsbox.porters.commons.interaction;

import java.io.Serializable;

public class Input<T extends Serializable> implements Action {

	private static final long serialVersionUID = 5102792261172066664L;

	private final String key;
	private final Class<T> type;
	private final T defaultValue;

	public Input(String key, Class<T> type, T defaultValue) {
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return this.key;
	}

	public Class<T> getType() {
		return this.type;
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}
}

package com.wmsbox.porters.commons.interaction;

import java.io.Serializable;

public abstract class TaskItem implements Serializable {

	private static final long serialVersionUID = -3584128677807060571L;

	private final String key;
	private final String text;

	public TaskItem(String key, String text) {
		this.key = key;
		this.text = text;
	}

	public String getKey() {
		return this.key;
	}

	public String getText() {
		return this.text;
	}
}

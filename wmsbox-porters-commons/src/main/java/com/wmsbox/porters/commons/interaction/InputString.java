package com.wmsbox.porters.commons.interaction;


public class InputString extends Action {

	private static final long serialVersionUID = 5102792261172066664L;

	private final String defaultValue;

	public InputString(String key, String text, String defaultValue) {
		super(key, text);
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public String toString() {
		return "InputString." + getKey() + "(" + this.defaultValue + ")";
	}
}

package com.wmsbox.porters.commons.interaction;


public class InputInteger extends Action {

	private static final long serialVersionUID = 5102792261172066664L;

	private final Integer defaultValue;

	public InputInteger(String key, String text, Integer defaultValue) {
		super(key, text);
		this.defaultValue = defaultValue;
	}

	public Integer getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public String toString() {
		return "InputInteger." + getKey() + "(" + this.defaultValue + ")";
	}
}

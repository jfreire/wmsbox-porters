package com.wmsbox.porters.commons.interaction;


public class Input extends Action {

	private static final long serialVersionUID = 5102792261172066664L;

	private final String initialValue;
	private final String type;
	private final InputMode mode;

	public Input(String key, String text, String type, InputMode mode, String initialValue) {
		super(key, text);
		this.initialValue = initialValue;
		this.type = type;
		this.mode = mode;
	}

	public InputMode getMode() {
		return this.mode;
	}

	public String getInitialValue() {
		return this.initialValue;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Input." + this.type + "." + getKey() + "(" + this.initialValue + ")";
	}
}

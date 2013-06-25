package com.wmsbox.porters.commons.interaction;

import java.util.Collections;
import java.util.Set;


public class Input extends Action {

	private static final long serialVersionUID = 5102792261172066664L;

	private final String initialValue;
	private final String type;
	private final InputMode mode;
	private final Set<InputOption> options;

	public Input(String key, String text, String type, InputMode mode, String initialValue, Set<InputOption> options) {
		super(key, text);
		this.initialValue = initialValue;
		this.type = type;
		this.mode = mode;
		this.options = options != null ? Collections.unmodifiableSet(options) : null;
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
	
	public Set<InputOption> getOptions() {
		return this.options;
	}

	@Override
	public String toString() {
		return "Input." + this.type + "." + getKey() + "(" + this.initialValue + ")";
	}
}

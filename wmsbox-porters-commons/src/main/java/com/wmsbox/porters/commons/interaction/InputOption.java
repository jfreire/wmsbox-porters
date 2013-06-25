package com.wmsbox.porters.commons.interaction;


public class InputOption extends OperationItem {

	private static final long serialVersionUID = -3005122435440668040L;

	private final String format;
	
	public InputOption(String key, String text, String format) {
		super(key, text);
		this.format = format;
	}

	public String getFormat() {
		return this.format;
	}
}

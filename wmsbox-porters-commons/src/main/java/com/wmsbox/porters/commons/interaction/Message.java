package com.wmsbox.porters.commons.interaction;


public class Message extends OperationItem {

	private static final long serialVersionUID = -8526618220797859655L;
	
	private final boolean error;

	public Message(String key, String text, boolean error) {
		super(key, text);
		this.error = error;
	}
	
	public boolean isError() {
		return this.error;
	}

	@Override
	public String toString() {
		return "Message." + getKey();
	}
}

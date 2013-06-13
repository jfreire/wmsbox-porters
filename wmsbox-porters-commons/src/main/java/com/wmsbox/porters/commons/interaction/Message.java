package com.wmsbox.porters.commons.interaction;


public class Message extends TaskItem {

	private static final long serialVersionUID = -8526618220797859655L;

	public Message(String key, String text) {
		super(key, text);
	}

	@Override
	public String toString() {
		return "Message." + getKey();
	}
}

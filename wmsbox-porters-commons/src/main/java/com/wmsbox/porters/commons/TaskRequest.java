package com.wmsbox.porters.commons;


public class TaskRequest extends Base {

	private static final long serialVersionUID = 7470255836771314636L;

	private final String patronKey;
	private final Long patronTaskId;
	private final String type;

	public TaskRequest(String patronKey, long patronTaskId, String type) {
		this.patronTaskId = patronTaskId;
		this.type = type;
		this.patronKey = patronKey;
	}

	public Long getPatronTaskId() {
		return this.patronTaskId;
	}

	public String getType() {
		return this.type;
	}

	public String getPatronKey() {
		return this.patronKey;
	}
}

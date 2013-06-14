package com.wmsbox.porters.commons;


public class OperationRequest extends Base {

	private static final long serialVersionUID = 7470255836771314636L;

	private final String patronKey;
	private final Long patronOperationId;
	private final String type;

	public OperationRequest(String patronKey, long patronOperationId, String type) {
		this.patronOperationId = patronOperationId;
		this.type = type;
		this.patronKey = patronKey;
	}

	public Long getPatronOperationId() {
		return this.patronOperationId;
	}

	public String getType() {
		return this.type;
	}

	public String getPatronKey() {
		return this.patronKey;
	}
}

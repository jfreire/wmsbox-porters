package com.wmsbox.porters.overseer;

import com.wmsbox.porters.commons.Operation;

public class SessionInfo {

	private final String porter;
	private final long loggedTime;
	private long lastActionTime;
	private Operation currentOperation;
	
	public SessionInfo(String porter, long loggedTime) {
		this.porter = porter;
		this.loggedTime = loggedTime;
	}

	public long getLastActionTime() {
		return this.lastActionTime;
	}

	public void setLastActionTime(long lastActionTime) {
		this.lastActionTime = lastActionTime;
	}

	public Operation getCurrentOperation() {
		return this.currentOperation;
	}

	public void setCurrentOperation(Operation currentOperation) {
		this.currentOperation = currentOperation;
	}

	public String getPorter() {
		return this.porter;
	}

	public long getLoggedTime() {
		return this.loggedTime;
	}
	
	public String toString() {
		return this.porter + ": " + this.currentOperation;
	}
}

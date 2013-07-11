package com.wmsbox.porters.overseer;

import com.wmsbox.porters.commons.Operation;

public class SessionInfo {

	private final String id;
	private final String porter;
	private final long loggedTime;
	private long lastActionTime;
	private Operation currentOperation;
	private boolean processingInPatron;

	public SessionInfo(String id, String porter, long loggedTime) {
		this.id = id;
		this.porter = porter;
		this.loggedTime = loggedTime;
	}

	public String getId() {
		return this.id;
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

	public synchronized void markAsReady() {
		this.processingInPatron = true;

	}

	public synchronized boolean checkThatIsTheFirst() throws InterruptedException {
		if (this.processingInPatron) {
			do {
				wait();
			} while (this.processingInPatron);

			return false;
		}

		return true;
	}

	public String getPorter() {
		return this.porter;
	}

	public long getLoggedTime() {
		return this.loggedTime;
	}

	@Override
	public String toString() {
		return this.porter + ": " + this.currentOperation;
	}
}

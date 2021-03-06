package com.wmsbox.porters.overseer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsbox.porters.commons.Operation;

public class SessionInfo {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionInfo.class);
	private static final long MAX_WAIT_TIME = 10000;

	private final String id;
	private final String porter;
	private final long loggedTime;
	private long lastActionTime;
	private Operation currentOperation;
	private boolean processingInPatron = false;

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
		LOGGER.debug(this.id + " markAsReady " + this.processingInPatron);
		this.processingInPatron = false;
		notifyAll();
	}

	public synchronized boolean checkThatIsTheFirst() throws InterruptedException {
		LOGGER.debug(this.id + " checkThatIsTheFirst " + this.processingInPatron);

		if (this.processingInPatron) {
			wait(MAX_WAIT_TIME);
			
			if (this.processingInPatron) {
			    throw new InterruptedException("By maxTime");
			}

			return false;
		}

		this.processingInPatron = true;

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

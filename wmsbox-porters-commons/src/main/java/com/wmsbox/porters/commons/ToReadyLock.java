package com.wmsbox.porters.commons;

public class ToReadyLock {

	private boolean ready = false;

	public void readyAndWaitToReady(ToReadyLock otherLock) {
		synchronized (otherLock) {
			this.ready = true;
			otherLock.ready = false;
			otherLock.notify();
		}

		synchronized (this) {
			if (!otherLock.ready) {
				try {
					wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public void end() {
		synchronized (this) {
			notify();
		}
	}
}

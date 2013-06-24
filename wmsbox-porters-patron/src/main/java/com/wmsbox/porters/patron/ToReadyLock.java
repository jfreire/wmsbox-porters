package com.wmsbox.porters.patron;

public class ToReadyLock {

	private boolean ready = false;

	public void readyAndWaitToReady(ToReadyLock otherLock) throws InterruptedException {
		
		synchronized (otherLock) {
			this.ready = true;
			otherLock.ready = false;
			otherLock.notify();
		}

		synchronized (this) {
			if (!otherLock.ready) {
				wait();
			}
		}
	}

	public void end(ToReadyLock otherLock) {
		synchronized (otherLock) {
			this.ready = true;
			otherLock.notify();
		}
	}
}

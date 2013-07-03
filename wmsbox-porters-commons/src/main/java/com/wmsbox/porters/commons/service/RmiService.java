package com.wmsbox.porters.commons.service;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RmiService<F> {
	
	private static final int DEFAULT_PORT = 8888;
	private static final int DEFAULT_PING_PERIOD = 2000;
	private static final int DEFAULT_WAIT_PERIOD_AFTER_TRAY = 5000;
	
	private final AtomicBoolean started = new AtomicBoolean();
	private int port = DEFAULT_PORT;
	private int pingPeriodInMillis = DEFAULT_PING_PERIOD;
	private int waitPeriodAfterTry = DEFAULT_WAIT_PERIOD_AFTER_TRAY;
	private Thread thread;
	private final RmiController<F> controller;
	
	public RmiService(RmiController<F> controller) {
		this.controller = controller;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPingPeriodInMillis() {
		return this.pingPeriodInMillis;
	}

	public void setPingPeriodInMillis(int pingPeriodInMillis) {
		this.pingPeriodInMillis = pingPeriodInMillis;
	}
	
	public int getWaitPeriodAfterTry() {
		return waitPeriodAfterTry;
	}

	public void setWaitPeriodAfterTry(int waitPeriodAfterTry) {
		this.waitPeriodAfterTry = waitPeriodAfterTry;
	}

	/**
	 * Obtains de facade.
	 * 
	 * @return
	 */
	public F get() {
		if (this.started.get()) {
			return this.controller.facade();
		}
		
		return null;
	}
	
	public void start() {
		if (this.started.compareAndSet(false, true)) {
			this.thread = new Thread(new RmiServiceWorker<F>(this, this.controller), 
					RmiServiceWorker.class.getName());
			this.thread.start();
		}
	}
	
	public void stop() {
		if (this.started.compareAndSet(true, false)) {
			this.thread.interrupt();
		}
	}
}

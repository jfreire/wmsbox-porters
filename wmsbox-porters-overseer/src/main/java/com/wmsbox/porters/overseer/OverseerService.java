package com.wmsbox.porters.overseer;

import java.util.concurrent.atomic.AtomicBoolean;

public class OverseerService {
	
	private OverseerController controller;
	private final AtomicBoolean started = new AtomicBoolean();
	private int port = 8888;
	private int pingPeriodInMillis = 2000;
	private Thread thread;
	
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
	
	public OverseerFacade facade() {
		if (this.started.get()) {
			return this.controller;
		}
		
		return null;
	}
	
	public void start() {
		if (this.started.compareAndSet(false, true)) {
			this.controller = new OverseerController();
			this.thread = new Thread(new OverseerServiceWorker(this, this.controller), 
					OverseerServiceWorker.class.getName());
			this.thread.start();
		}
	}
	
	public void stop() {
		if (this.started.compareAndSet(true, false)) {
			this.thread.interrupt();
		}
	}
}

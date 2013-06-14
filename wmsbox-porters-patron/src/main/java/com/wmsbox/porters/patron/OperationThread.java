package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.ToReadyLock;

class OperationThread implements Runnable {

	private final OperationController controller;
	private final ToReadyLock controllerLock = new ToReadyLock();
	private final ToReadyLock connectionLock = new ToReadyLock();
	private Operation operation;
	private Thread thread;

	public OperationThread(OperationController controller, Operation operation) {
		this.controller = controller;
		this.operation = operation;
		controller.init(this);
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
		this.connectionLock.readyAndWaitToReady(this.controllerLock);
	}

	public Operation getOperation() {
		return this.operation;
	}

	public void interactReturn(Operation operation) {
		System.out.println("interactReturn " + operation);
		this.operation = operation;
		this.connectionLock.readyAndWaitToReady(this.controllerLock);
		System.out.println("interactReturn End " + operation);
	}

	public void run() {
		try {
			this.controller.process();
		} catch (InterruptedException e) {
			this.thread.interrupt();
		}
	}

	public void cancel() {
		this.thread.interrupt();
	}

	public void requestIteration() throws InterruptedException {
		System.out.println("requestIteration " + this.operation);
		this.controllerLock.readyAndWaitToReady(this.connectionLock);
	}
}
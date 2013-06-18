package com.wmsbox.porters.patron;

import java.util.Arrays;

import com.wmsbox.porters.commons.Operation;

class OperationThread implements Runnable {

	private final OperationController controller;
	private final ToReadyLock controllerLock = new ToReadyLock();
	private final ToReadyLock connectionLock = new ToReadyLock();
	private OperationController nextController;
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
		
		try {
			this.connectionLock.readyAndWaitToReady(this.controllerLock);
		} catch (InterruptedException e) {
			this.thread.interrupt();
		}
	}

	public Operation getOperation() {
		return this.operation;
	}

	public void interactReturn(Operation operation) {
		System.out.println("interactReturn " + operation);
		this.operation = operation;
		
		try {
			this.connectionLock.readyAndWaitToReady(this.controllerLock);
		} catch (InterruptedException e) {
			this.thread.interrupt();
		}
		
		System.out.println("interactReturn End " + operation);
	}

	public void run() {
		try {
			this.nextController = this.controller.process();
			
			if (this.operation.getState().isLive()) {
				this.operation.completed(null);
			}
		} catch (InterruptedException e) {
			this.thread.interrupt();
		} catch (Exception e) {
			this.operation.cancelByPatron(null); //TODO enviar error
		} finally {
			this.connectionLock.end();
		}
	}

	public void cancel() {
		System.out.println("Cancel " + Arrays.toString(this.thread.getStackTrace()));
		this.thread.interrupt();
	}

	public Operation requestIteration() throws InterruptedException {
		System.out.println("requestIteration " + this.operation);
		this.controllerLock.readyAndWaitToReady(this.connectionLock);

		return this.operation;
	}

	public OperationController getNextController() {
		return this.nextController;
	}
}
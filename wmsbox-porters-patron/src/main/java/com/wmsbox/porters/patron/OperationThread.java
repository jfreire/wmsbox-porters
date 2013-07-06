package com.wmsbox.porters.patron;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsbox.porters.commons.Operation;

class OperationThread implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationThread.class);

	private final OperationController controller;
	private final ToReadyLock controllerLock = new ToReadyLock();
	private final ToReadyLock connectionLock = new ToReadyLock();
	private OperationController nextController;
	private Operation operation;
	private final String resourcesFile;
	private Thread thread;

	public OperationThread(OperationController controller, Operation operation,
			String resourcesFile) {
		this.controller = controller;
		this.operation = operation;
		this.resourcesFile = resourcesFile;
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
		LOGGER.info("interactReturn " + operation);
		this.operation = operation;
		
		try {
			this.connectionLock.readyAndWaitToReady(this.controllerLock);
		} catch (InterruptedException e) {
			this.thread.interrupt();
		}
		
		LOGGER.info("interactReturn End " + operation);
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
			LOGGER.error(e.getMessage(), e);
			this.controller.canceled("exception", e.getMessage());
		} finally {
			this.controllerLock.end(this.connectionLock);
		}
	}

	public void cancel() {
		LOGGER.info("Cancel " + Arrays.toString(this.thread.getStackTrace()));
		this.thread.interrupt();
	}

	public Operation requestIteration() throws InterruptedException {
		LOGGER.debug("requestIteration " + this.operation);
		this.controllerLock.readyAndWaitToReady(this.connectionLock);

		return this.operation;
	}

	public OperationController getNextController() {
		return this.nextController;
	}

	public String getResourcesFile() {
		return resourcesFile;
	}
}
package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.Task;
import com.wmsbox.porters.commons.ToReadyLock;

class TaskThread implements Runnable {

	private final TaskController controller;
	private final ToReadyLock taskLock = new ToReadyLock();
	private final ToReadyLock connectionLock = new ToReadyLock();
	private Task task;
	private Thread thread;

	public TaskThread(TaskController controller, Task task) {
		this.controller = controller;
		this.task = task;
		controller.setTaskThread(this);
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
		this.connectionLock.readyAndWaitToReady(this.taskLock);
	}

	public Task getTask() {
		return this.task;
	}

	public void interactReturn(Task task) {
		System.out.println("interactReturn " + task);
		this.task = task;
		this.connectionLock.readyAndWaitToReady(this.taskLock);
		System.out.println("interactReturn End " + task);
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
		System.out.println("requestIteration " + this.task);
		this.taskLock.readyAndWaitToReady(this.connectionLock);
	}
}
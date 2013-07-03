package com.wmsbox.porters.commons.service;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RmiServiceWorker<F> implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(RmiServiceWorker.class);
	
	private final RmiController<F> controller;
	private final RmiService<F> service;

	public RmiServiceWorker(RmiService<F> service, RmiController<F> controller) {
		this.service = service;
		this.controller = controller;
	}

	public void run() {
		boolean previousError = false;
		Thread thread = Thread.currentThread();

		try {
			while (!thread.isInterrupted()) {
				if (this.controller.isConnected()) {
					try {
						this.controller.doPing();
						
						Thread.sleep(this.service.getPingPeriodInMillis());
					} catch (RemoteException e) {
						LOGGER.error(e.getMessage());
						this.controller.disconnect(e);
					}
				} else {
					Exception exception = null;

					try {
						this.controller.tryConnect(this.service);
						LOGGER.info("Connected/Started");
						previousError = false;
					} catch (RemoteException e) {
						exception = e;
					} catch (NotBoundException e) {
						exception = e;
					}

					if (exception != null) {
						if (!previousError) {
							LOGGER.error(exception.getMessage());
							previousError = true;
						}

						Thread.sleep(this.service.getWaitPeriodAfterTry());
					}
				}
			}
		} catch (InterruptedException e) {
			thread.interrupt();
		} finally {
			this.controller.stopped();
		}
	}
}
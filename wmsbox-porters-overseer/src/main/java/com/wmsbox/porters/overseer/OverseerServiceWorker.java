package com.wmsbox.porters.overseer;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsbox.porters.commons.OverseerRemote;

class OverseerServiceWorker implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverseerServiceWorker.class);
	
	private final OverseerController controller;
	private final OverseerService service;
	private OverseerRemote overseerStub;
	private boolean connected = false;

	public OverseerServiceWorker(OverseerService service, OverseerController controller) {
		this.service = service;
		this.controller = controller;
	}

	public void run() {
		boolean previousError = false;
		Thread thread = Thread.currentThread();

		try {
			while (!thread.isInterrupted()) {
				if (this.connected) {
					try {
						this.controller.pingPatrons();
						
						Thread.sleep(this.service.getPingPeriodInMillis());
					} catch (RemoteException e) {
						LOGGER.error(e.getMessage());
						this.connected = false;
					}
				} else {
					Exception exception = null;

					try {
						this.overseerStub = (OverseerRemote) UnicastRemoteObject.exportObject(this.controller,
								this.service.getPort());
						Registry registry = LocateRegistry.createRegistry(this.service.getPort());
						registry.rebind(OverseerRemote.REMOTE_REFERENCE_NAME, this.overseerStub);
						
						this.connected = true;
						previousError = false;
					} catch (RemoteException e) {
						exception = e;
					}

					if (exception != null) {
						if (!previousError) {
							LOGGER.error(exception.getMessage());
							previousError = true;
						}

						Thread.sleep(5000);
					}
				}
			}
		} catch (InterruptedException e) {
			thread.interrupt();
		} finally {
			try {
				this.controller.cancelAll();
				UnicastRemoteObject.unexportObject(this.overseerStub, false);
			} catch (NoSuchObjectException e) {
				// Nada
			}
		}
	}
}
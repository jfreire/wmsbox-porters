package com.wmsbox.porters.patron;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;

//TODO ping
public class OverseerConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverseerConnector.class);

	private final AtomicBoolean started = new AtomicBoolean(false);
	private String host;
	private int port;
	private Thread thread;

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start(final Patron patron) {
		if (this.started.compareAndSet(false, true)) {
			this.thread = new Thread(new Runnable() {
				private boolean connected = false;

				public void run() {
					boolean previousError = false;

					while (!this.connected) {
						Exception exception = null;

						try {
							final Registry registry = LocateRegistry
									.getRegistry(OverseerConnector.this.host,
									OverseerConnector.this.port);
							final Remote remote = registry.lookup(OverseerRemote.REMOTE_REFERENCE_NAME);
							final OverseerRemote overseer = OverseerRemote.class.cast(remote);
							PatronRemote patronRemote = new PatronRemoteImpl(patron, overseer);
							PatronRemote stub = (PatronRemote) UnicastRemoteObject
									.exportObject(patronRemote, 0);
							overseer.register(stub);
							this.connected = true;
							LOGGER.info("Connected");
							previousError = false;

							synchronized (OverseerConnector.this.thread) {
								OverseerConnector.this.thread.wait();
								this.connected = false;
								UnicastRemoteObject.unexportObject(stub, false);
								LOGGER.info("Disconnected");
							}
						} catch (RemoteException e) {
							exception = e;
						} catch (NotBoundException e) {
							exception = e;
						} catch (InterruptedException e) {
							OverseerConnector.this.thread.interrupt();
						}

						if (exception != null) {
							if (!previousError) {
								LOGGER.error(exception.getMessage());
								previousError = true;
							}

							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								OverseerConnector.this.thread.interrupt();
							}
						}
					}
				}
			} );
			this.thread.start();
		}
	}

	public void stop() {
		LOGGER.info("Go to stop");
		this.thread.interrupt();
	}

	public void disconnected(RemoteException exception) {
		LOGGER.error("Disconnecting by {} ", exception.getMessage());

		synchronized (OverseerConnector.this.thread) {
			this.thread.notify();
		}
	}
}

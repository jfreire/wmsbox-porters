package com.wmsbox.porters.patron;

import java.rmi.NoSuchObjectException;
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

public class OverseerConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverseerConnector.class);

	private final AtomicBoolean started = new AtomicBoolean(false);
	private String host;
	private int port;
	private long pingPeriodInMillis = 1000;
	private Thread thread;
	private PatronRemote patronStub;
	private OverseerRemote overseerStub;
	private OverseerWorker worker;

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

	public long getPingPeriodInMillis() {
		return pingPeriodInMillis;
	}

	public void setPingPeriodInMillis(long pingPeriodInMillis) {
		this.pingPeriodInMillis = pingPeriodInMillis;
	}

	public void start(Patron patron) {
		if (this.started.compareAndSet(false, true)) {
			this.worker = new OverseerWorker(this, patron);
			this.thread = new Thread(this.worker, this.worker.getClass().getSimpleName());

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

	private void connect(Patron patron) throws RemoteException, NotBoundException {
		final Registry registry = LocateRegistry
				.getRegistry(this.host, this.port);
		final Remote remote = registry.lookup(OverseerRemote.REMOTE_REFERENCE_NAME);
		this.overseerStub = OverseerRemote.class.cast(remote);
		PatronRemote patronRemote = new PatronRemoteImpl(patron, this.overseerStub);
		this.patronStub = (PatronRemote) UnicastRemoteObject
				.exportObject(patronRemote, 0);
		this.overseerStub.register(this.patronStub);
		LOGGER.info("Connected");
	}

	private void unregister() throws NoSuchObjectException {
		UnicastRemoteObject.unexportObject(this.patronStub, false);
	}

	private void ping() throws InterruptedException, RemoteException {
		synchronized (this.worker) {
			this.worker.wait(this.pingPeriodInMillis);
		}

		this.overseerStub.ping();
	}

	public static class OverseerWorker implements Runnable {

		private final Patron patron;
		private final OverseerConnector connector;
		private boolean connected = false;

		public OverseerWorker(OverseerConnector connector, Patron patron) {
			this.patron = patron;
			this.connector = connector;
		}

		public void run() {
			boolean previousError = false;
			Thread thread = Thread.currentThread();

			try {
				while (!thread.isInterrupted()) {
					if (this.connected) {
						try {
							this.connector.ping();
						} catch (RemoteException e) {
							LOGGER.error(e.getMessage());
							this.connected = false;
						}
					} else {
						Exception exception = null;

						try {
							this.connector.connect(this.patron);
							this.connected = true;
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

							Thread.sleep(5000);
						}
					}
				}
			} catch (InterruptedException e) {
				thread.interrupt();
			} finally {
				try {
					this.connector.unregister();
				} catch (NoSuchObjectException e) {
					// Nada
				}
			}
		}
	}
}

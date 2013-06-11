package com.wmsbox.porters.patron;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;

public class OverseerConnector {

	private final AtomicBoolean starting = new AtomicBoolean(false);
	private boolean started = false;
	private String host;
	private int port;

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

	public void start(Patron patron) throws RemoteException, NotBoundException {
		if (!this.started && this.starting.compareAndSet(false, true)) {
			final Registry registry = LocateRegistry.getRegistry(this.host, this.port);
			final Remote remote = registry.lookup(OverseerRemote.REMOTE_REFERENCE_NAME);
			final OverseerRemote overseer = OverseerRemote.class.cast(remote);
			PatronRemote patronRemote = new PatronRemoteImpl(patron, overseer);
			PatronRemote stub = (PatronRemote) UnicastRemoteObject.exportObject(patronRemote, 0);

			overseer.register(stub);
			System.out.println("Registrado");
			this.started = true;
			this.starting.set(false);
		}
	}

	public void stop() {
		//TODO
	}
}

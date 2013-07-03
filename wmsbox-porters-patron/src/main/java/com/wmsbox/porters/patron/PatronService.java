package com.wmsbox.porters.patron;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.service.RmiController;
import com.wmsbox.porters.commons.service.RmiService;

public class PatronService extends RmiService<PatronFacade> {

	private String host;
	private Patron patron;
	
	public PatronService() {
		super(new RmiController<PatronFacade>() {
			private PatronRemote patronStub;
			private OverseerRemote overseerStub;
			private boolean connected;

			public void doPing() throws RemoteException {
				this.overseerStub.ping();				
			}

			public void stopped() {
				try {
					UnicastRemoteObject.unexportObject(this.patronStub, false);
				} catch (NoSuchObjectException e) {
					// Nada
				}
			}

			public boolean isConnected() {
				return this.connected;
			}

			public void disconnect(Exception cause) {
				this.connected = false;
			}

			public void tryConnect(RmiService<PatronFacade> service) throws RemoteException, NotBoundException {
				PatronService patronService = (PatronService) service;
				final Registry registry = LocateRegistry
						.getRegistry(patronService.getHost(), service.getPort());
				Remote remote = registry.lookup(OverseerRemote.REMOTE_REFERENCE_NAME);
				this.overseerStub = OverseerRemote.class.cast(remote);
				PatronRemote patronRemote = new PatronRemoteImpl(patronService.patron, this.overseerStub);
				this.patronStub = (PatronRemote) UnicastRemoteObject.exportObject(patronRemote, 0);
				this.overseerStub.register(this.patronStub);
				
				this.connected = true;
			}

			public PatronFacade facade() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	}
	
	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public void init(Patron patron) {
		this.patron = patron;
	}
}

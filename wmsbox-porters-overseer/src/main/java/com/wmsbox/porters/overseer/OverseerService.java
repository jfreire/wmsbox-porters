package com.wmsbox.porters.overseer;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.service.RmiController;
import com.wmsbox.porters.commons.service.RmiService;

public class OverseerService extends RmiService<OverseerFacade> {
	
	public OverseerService() {
		super(new RmiController<OverseerFacade>() {
			
			private Registry registry;
			private boolean connected = false;
			private OverseerController controller = new OverseerController();

			public void doPing() throws RemoteException {
				this.controller.pingPatrons();
			}

			public void stopped() {
				this.controller.cancelAll();
				
				try {	
					UnicastRemoteObject.unexportObject(this.controller, true);
				} catch (NoSuchObjectException e) {
					// Nada
				}
				
				try {	
					UnicastRemoteObject.unexportObject(this.registry, true);
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

			public void tryConnect(RmiService<OverseerFacade> service) throws RemoteException {
				OverseerRemote overseerStub = (OverseerRemote) UnicastRemoteObject
						.exportObject(this.controller, service.getPort());
				this.registry = LocateRegistry.createRegistry(service.getPort());
				this.registry.rebind(OverseerRemote.REMOTE_REFERENCE_NAME, overseerStub);
				
				this.connected = true;
			}

			public OverseerFacade facade() {
				return this.controller;
			}
		});
	}
}

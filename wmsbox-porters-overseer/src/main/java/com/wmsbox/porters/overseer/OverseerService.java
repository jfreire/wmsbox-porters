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
			
			private OverseerRemote overseerStub;
			private boolean connected = false;
			private OverseerController controller = new OverseerController();

			public void doPing() throws RemoteException {
				this.controller.pingPatrons();
			}

			public void stopped() {
				try {
					this.controller.cancelAll();
					UnicastRemoteObject.unexportObject(this.overseerStub, false);
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
				this.overseerStub = (OverseerRemote) UnicastRemoteObject.exportObject(this.controller,
						service.getPort());
				Registry registry = LocateRegistry.createRegistry(service.getPort());
				registry.rebind(OverseerRemote.REMOTE_REFERENCE_NAME, this.overseerStub);
				
				this.connected = true;
			}

			public OverseerFacade facade() {
				return this.controller;
			}
		});
	}
}

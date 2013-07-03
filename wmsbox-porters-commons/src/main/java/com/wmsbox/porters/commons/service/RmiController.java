package com.wmsbox.porters.commons.service;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface RmiController<F> {

	void doPing() throws RemoteException;
	
	void stopped();
	
	boolean isConnected();
	
	void disconnect(Exception cause);
	
	void tryConnect(RmiService<F> service) throws RemoteException, NotBoundException;

	F facade();
}

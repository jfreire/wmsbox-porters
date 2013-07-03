package com.wmsbox.porters.commons.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiFacade extends Remote {

	void ping() throws RemoteException;
}

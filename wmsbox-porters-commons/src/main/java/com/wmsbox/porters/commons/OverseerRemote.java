package com.wmsbox.porters.commons;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OverseerRemote extends Remote {

	String REMOTE_REFERENCE_NAME = "Overseer";

	void register(PatronRemote patron) throws RemoteException;

	void request(OperationRequest request) throws RemoteException;

	Operation createOperation(OperationType type, Context context) throws RemoteException;
}

package com.wmsbox.porters.commons;

import java.rmi.RemoteException;

import com.wmsbox.porters.commons.service.RmiFacade;

public interface OverseerRemote extends RmiFacade {

	String REMOTE_REFERENCE_NAME = "Overseer";

	void register(PatronRemote patron) throws RemoteException;

	void request(OperationRequest request) throws RemoteException;

	Operation createOperation(OperationType type, Context context) throws RemoteException;
}

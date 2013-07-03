package com.wmsbox.porters.commons;

import java.rmi.RemoteException;
import java.util.List;

import com.wmsbox.porters.commons.service.RmiFacade;

public interface PatronRemote extends RmiFacade {

	String getKey() throws RemoteException;

	List<OperationType> getOperationTypes() throws RemoteException;

	Operation porterInteracts(Operation operation) throws RemoteException;

	Operation porterRequestOperation(String code, Context ctx) throws RemoteException;

	Operation porterRequestOperation(OperationType type, Context ctx) throws RemoteException;

	void cancel(Operation operation) throws RemoteException;
}

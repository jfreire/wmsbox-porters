package com.wmsbox.porters.commons;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PatronRemote extends Remote {

	String getKey() throws RemoteException;

	List<OperationType> getOperationTypes() throws RemoteException;

	Operation porterIteracts(Operation operation) throws RemoteException;

	Operation porterRequestOperation(String code, Context ctx) throws RemoteException;

	void ping() throws RemoteException;

	Operation porterRequestOperation(OperationType type, Context ctx) throws RemoteException;

	void cancel(Operation operation) throws RemoteException;
}

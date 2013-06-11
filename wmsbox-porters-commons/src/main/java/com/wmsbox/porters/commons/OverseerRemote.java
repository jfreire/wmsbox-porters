package com.wmsbox.porters.commons;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OverseerRemote extends Remote {

	String REMOTE_REFERENCE_NAME = "Overseer";

	void register(PatronRemote patron) throws RemoteException;

	void request(TaskRequest request) throws RemoteException;

	Task createTask(TaskTypeCode type, String porter) throws RemoteException;
}

package com.wmsbox.porters.commons;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PatronRemote extends Remote {

	String getKey() throws RemoteException;

	List<TaskTypeCode> getTaskTypes() throws RemoteException;

	Task porterIteracts(Task task) throws RemoteException;

	Task porterRequestTask(String code, Context ctx) throws RemoteException;

	Task porterRequestTask(TaskTypeCode type, Context ctx) throws RemoteException;

	void cancel(Task task) throws RemoteException;
}

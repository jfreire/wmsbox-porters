package com.wmsbox.porters.commons;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PatronRemote extends Remote {

	String getKey() throws RemoteException;

	List<TaskTypeCode> getTaskTypes() throws RemoteException;

	void porterIteracts(Task task) throws RemoteException;

	Task porterRequestTask(String code, String porter) throws RemoteException;

	Task porterRequestTask(TaskTypeCode type, String porter) throws RemoteException;
}

package com.wmsbox.porters.overseer;

import java.rmi.RemoteException;
import java.util.List;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationType;

public interface OverseerFacade {

	void login(String porter);
	
	void logout(String porter);
	
	List<SessionInfo> findCurrentSessions();
	
	void cancelCurrentOperation(String porter);
	
	Operation porterIteracts(Operation operation) throws RemoteException;

	Operation porterRequestOperation(String code, Context ctx) throws RemoteException;

	Operation porterRequestOperation(OperationType type, Context ctx) throws RemoteException;
}

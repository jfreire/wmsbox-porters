package com.wmsbox.porters.patron;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;

public class PatronRemoteImpl implements PatronRemote {

	private final Patron patron;
	private final OverseerRemote overseer;
	private final Map<Long, OperationThread> operations = new HashMap<Long, OperationThread>();

	public PatronRemoteImpl(Patron patron, OverseerRemote overseer) {
		this.patron = patron;
		this.overseer = overseer;
	}

	public String getKey() {
		return this.patron.getKey();
	}

	public List<OperationType> getOperationTypes() {
		return this.patron.getOperationTypes();
	}

	public Operation porterIteracts(Operation operation) throws RemoteException {
		OperationThread taskThread = this.operations.get(operation.getId());
		taskThread.interactReturn(operation);

		if (!operation.getState().isLive()) {
			OperationController nextController = taskThread.getNextController();
			this.operations.remove(operation.getId());

			if (nextController == null) {
				return null;
			} else {
				operation = startTask(nextController, operation.getContext());
			}
		}

		return operation;
	}

	public Operation porterRequestOperation(String code, Context ctx) throws RemoteException {
		OperationController controller = this.patron.porterRequestOperation(code);

		if (controller == null) {
			return null;
		}

		return startTask(controller, ctx);
	}

	public Operation porterRequestOperation(OperationType type, Context ctx) throws RemoteException {
		OperationController controller = this.patron.porterRequestOperation(type);

		return startTask(controller, ctx);
	}

	private Operation startTask(OperationController controller, Context ctx) throws RemoteException {
		Operation operation = this.overseer.createOperation(controller.getType(), ctx);
		OperationThread thread = new OperationThread(controller, operation);
		this.operations.put(operation.getId(), thread);
		operation.goToProcess();
		thread.start();

		return operation;
	}

	public void cancel(Operation operation) throws RemoteException {
		OperationThread operationThread = this.operations.get(operation.getId());
		operationThread.cancel();
	}

	public void ping() throws RemoteException {
		// Nada
	}
}

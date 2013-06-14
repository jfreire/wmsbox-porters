package com.wmsbox.porters.patron;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationType;

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

	public Operation porterIteracts(Operation operation) {
		OperationThread taskThread = this.operations.get(operation.getId());
		taskThread.interactReturn(operation);

		return operation;
	}

	public Operation porterRequestOperation(String code, Context ctx) {
		OperationController controller = this.patron.porterRequestOperation(code);

		return startTask(controller, ctx);
	}

	public Operation porterRequestOperation(OperationType type, Context ctx) {
		OperationController controller = this.patron.porterRequestOperation(type);

		return startTask(controller, ctx);
	}

	private Operation startTask(OperationController controller, Context ctx) {
		Operation operation;

		try {
			operation = this.overseer.createOperation(controller.getType(), ctx);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}

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
}

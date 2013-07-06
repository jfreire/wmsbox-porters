package com.wmsbox.porters.patron;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;

public class PatronRemoteImpl implements PatronRemote {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatronRemoteImpl.class);
	
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

	public Operation porterInteracts(Operation operation) throws RemoteException {
		LOGGER.info("porterInteracts " + operation.getId());
		OperationThread taskThread = this.operations.get(operation.getId());
		taskThread.interactReturn(operation);
		LOGGER.info("porterInteracts server end " + operation.getId());

		if (!operation.getState().isLive()) {
			OperationController nextController = taskThread.getNextController();
			this.operations.remove(operation.getId());

			if (nextController != null) {
				Operation newOperation = startTask(nextController, operation.getContext());
				newOperation.setMessage(operation.getMessage());

				return newOperation;
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
		OperationThread thread = new OperationThread(controller, operation, 
				this.patron.getResourcesFile());
		this.operations.put(operation.getId(), thread);
		operation.goToProcess();
		thread.start();

		return operation;
	}

	public void cancel(Operation operation) throws RemoteException {
		LOGGER.info("cancel " + operation.getId());
		OperationThread operationThread = this.operations.get(operation.getId());

		if (operationThread != null) {
			operationThread.cancel();
		}
	}

	public void ping() throws RemoteException {
		// Nada
	}
}

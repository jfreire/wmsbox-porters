package com.wmsbox.porters.overseer;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationRequest;
import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;

public class OverseerServer implements OverseerRemote {

	private static final Logger LOGGER = LoggerFactory.getLogger(OverseerServer.class);

	public static final OverseerServer INSTANCE = new OverseerServer();

	private final AtomicLong ids = new AtomicLong();
	private final Map<Long, Operation> operations = new TreeMap<Long, Operation>();
	private PatronRemote patron = null;

	private OverseerServer() {

	}

	public void register(PatronRemote patron) throws RemoteException {
		LOGGER.info("Registrado ..... {} ", patron.getKey());
		this.patron = patron;
	}

	public Operation porterRequestOperation(String code, Context ctx) throws RemoteException {
		Operation operation = null;

		if (this.patron != null) {
			operation = this.patron.porterRequestOperation(code, ctx);

			if (operation != null) {
				this.operations.put(operation.getId(), operation);
			}
		}

		return operation;
	}

	public Operation porterRequestOperation(OperationType type, Context ctx)
			throws RemoteException {
		Operation operation = null;

		if (this.patron != null) {
			operation = this.patron.porterRequestOperation(type, ctx);

			if (operation != null) {
				this.operations.put(operation.getId(), operation);
			}
		}

		return operation;
	}

	public Operation porterIteracts(Operation operation) throws RemoteException {
		if (this.patron != null) {
			operation = this.patron.porterIteracts(operation);

			if (operation != null) {
				this.operations.put(operation.getId(), operation);
			}
		}

		return operation;
	}

	public List<OperationType> getOperationTypes() throws RemoteException {
		if (this.patron != null) {
			return this.patron.getOperationTypes();
		}

		return Collections.emptyList();
	}

	public void request(OperationRequest request) {
		throw new UnsupportedOperationException();
	}

	public Operation createOperation(OperationType type, Context context) {
		return new Operation(this.ids.getAndIncrement(), type, context);
	}

	public Collection<Operation> operations() {
		return this.operations.values();
	}

	public void cancelAll() {
		for (Operation operation : this.operations.values()) {
			if (operation.getState().isLive()) {
				cancel(operation);
			}
		}
	}

	public void cancel(Operation operation) {
		LOGGER.info("Cancelando ..... {} ", operation.getId());
		operation.cancelByPatron(null);

		try {
			this.patron.cancel(operation);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ping() throws RemoteException {
		// Nada
	}
}


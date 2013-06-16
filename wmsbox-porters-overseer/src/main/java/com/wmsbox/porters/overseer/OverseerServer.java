package com.wmsbox.porters.overseer;

import java.rmi.RemoteException;
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
	private PatronRemote patron = null;

	private OverseerServer() {

	}

	public void register(PatronRemote patron) throws RemoteException {
		LOGGER.info("Registrado ..... {} ", patron.getKey());
		this.patron = patron;
	}

	public PatronRemote patron() {
		return this.patron;
	}

	public void request(OperationRequest request) {
		throw new UnsupportedOperationException();
	}

	public Operation createOperation(OperationType type, Context context) {
		return new Operation(this.ids.getAndIncrement(), type, context);
	}
}


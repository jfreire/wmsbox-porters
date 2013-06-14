package com.wmsbox.porters.overseer;

import java.util.concurrent.atomic.AtomicLong;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationRequest;
import com.wmsbox.porters.commons.OperationType;

public class OverseerServer implements OverseerRemote {

	public static final OverseerServer INSTANCE = new OverseerServer();

	private final AtomicLong ids = new AtomicLong();
	private PatronRemote patron = null;

	private OverseerServer() {

	}

	public void register(PatronRemote patron) {
		System.out.println("registrado .....");
		this.patron = patron; //TODO futuro varios patrones.
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


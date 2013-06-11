package com.wmsbox.porters.overseer;

import java.util.concurrent.atomic.AtomicLong;

import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.Task;
import com.wmsbox.porters.commons.TaskRequest;
import com.wmsbox.porters.commons.TaskTypeCode;

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

	public void request(TaskRequest request) {
		throw new UnsupportedOperationException();
	}

	public Task createTask(TaskTypeCode type, String porter) {
		return new Task(this.ids.getAndIncrement(), type, porter);
	}
}


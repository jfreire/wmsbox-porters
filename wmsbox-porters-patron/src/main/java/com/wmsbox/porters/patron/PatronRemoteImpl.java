package com.wmsbox.porters.patron;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wmsbox.porters.commons.Context;
import com.wmsbox.porters.commons.OverseerRemote;
import com.wmsbox.porters.commons.PatronRemote;
import com.wmsbox.porters.commons.Task;
import com.wmsbox.porters.commons.TaskTypeCode;

public class PatronRemoteImpl implements PatronRemote {

	private final Patron patron;
	private final OverseerRemote overseer;
	private final Map<Long, TaskThread> tasks = new HashMap<Long, TaskThread>();

	public PatronRemoteImpl(Patron patron, OverseerRemote overseer) {
		this.patron = patron;
		this.overseer = overseer;
	}

	public String getKey() {
		return this.patron.getKey();
	}

	public List<TaskTypeCode> getTaskTypes() {
		return this.patron.getTaskTypes();
	}

	public Task porterIteracts(Task task) {
		TaskThread taskThread = this.tasks.get(task.getId());
		taskThread.interactReturn(task);

		return task;
	}

	public Task porterRequestTask(String code, Context ctx) {
		TaskController controller = this.patron.porterRequestTask(code);

		return startTask(controller, ctx);
	}

	public Task porterRequestTask(TaskTypeCode type, Context ctx) {
		TaskController controller = this.patron.porterRequestTask(type);

		return startTask(controller, ctx);
	}

	private Task startTask(TaskController controller, Context ctx) {
		Task task;

		try {
			task = this.overseer.createTask(controller.getType(), ctx);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}

		TaskThread thread = new TaskThread(controller, task);
		this.tasks.put(task.getId(), thread);
		task.goToProcess();
		thread.start();

		return task;
	}

	public void cancel(Task task) throws RemoteException {
		TaskThread taskThread = this.tasks.get(task.getId());
		taskThread.cancel();
	}
}

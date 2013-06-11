package com.wmsbox.porters.patron;

import java.io.Serializable;

import com.wmsbox.porters.commons.Task;
import com.wmsbox.porters.commons.TaskTypeCode;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Confirm;
import com.wmsbox.porters.commons.interaction.Input;
import com.wmsbox.porters.commons.interaction.Message;


public abstract class TaskController {

	private TaskThread taskThread;
	private final TaskTypeCode type;

	public TaskController(TaskTypeCode type) {
		this.type = type;
	}

	protected final TaskTypeCode getType() {
		return this.type;
	}

	protected final void setTaskThread(TaskThread taskThread) {
		this.taskThread = taskThread;
	}

	/**
	 *
	 * @return La siguiente tarea o null.
	 * @throws InterruptedException
	 */
	public abstract TaskController process() throws InterruptedException;

	@SuppressWarnings("unchecked")
	public final <T extends Serializable> T input(Class<T> type, String key)
			throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(new Input<T>(key, type, null));

		this.taskThread.requestIteration();

		return (T) task.getPorderDoValue();
	}

	@SuppressWarnings("unchecked")
	public final <T extends Serializable> T input(Class<T> type, String key, T defaultValue)
			throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(new Input<T>(key, type, defaultValue));

		this.taskThread.requestIteration();

		return (T) task.getPorderDoValue();
	}

	/**
	 *
	 * @param actionKeys pueden ser opciones o un input y opciones.
	 * @return Resultado de la entrada o el botón pulsado
	 * @throws InterruptedException
	 */
	public final Serializable choose(Action... actionKeys) throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(actionKeys);

		this.taskThread.requestIteration();

		return task.getPorderDoValue() != null ? task.getPorderDoValue() : task.getPorterDo();
	}

	public final boolean confirm(String key, Serializable... params) throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(new Confirm(key, params));

		this.taskThread.requestIteration();

		return task.getPorderDoValue() != null ? Boolean.TRUE == task.getPorderDoValue() : false;
	}

	public final void error(String key, Serializable... params) {
		Task task = this.taskThread.getTask();
		task.error(new Message(key, params));
	}

	public void info(int index, String key, Serializable... params) {
		Task task = this.taskThread.getTask();
		task.info(index, new Message(key, params));
	}
}

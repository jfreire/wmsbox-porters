package com.wmsbox.porters.patron;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.wmsbox.porters.commons.Task;
import com.wmsbox.porters.commons.TaskTypeCode;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Confirm;
import com.wmsbox.porters.commons.interaction.InputInteger;
import com.wmsbox.porters.commons.interaction.InputString;
import com.wmsbox.porters.commons.interaction.Message;

public abstract class TaskController {

	private final TaskTypeCode type;
	private TaskThread taskThread;

	public TaskController(TaskTypeCode type) {
		this.type = type;
	}

	protected final TaskTypeCode getType() {
		return this.type;
	}

	protected final void init(TaskThread taskThread) {
		this.taskThread = taskThread;
	}

	/**
	 *
	 * @return La siguiente tarea o null.
	 * @throws InterruptedException
	 */
	public abstract TaskController process() throws InterruptedException;

	public final String inputString(String key)	throws InterruptedException {
		return inputString(key, null);
	}

	public final String inputString(String key, String defaultValue)
			throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(new InputString(key, text(key), null));

		this.taskThread.requestIteration();

		return (String) task.getPorderDoValue();
	}

	public final Integer inputInteger(String key) throws InterruptedException {
		return inputInteger(key, null);
	}

	public final Integer inputInteger(String key, Integer defaultValue)
			throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(new InputInteger(key, text(key), defaultValue));

		this.taskThread.requestIteration();

		return (Integer) task.getPorderDoValue();
	}

	/**
	 *
	 * @param actionKeys pueden ser opciones o un input y opciones.
	 * @return Resultado de la entrada o el botón pulsado
	 * @throws InterruptedException
	 */
	public final Object choose(Action... actionKeys) throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(actionKeys);

		this.taskThread.requestIteration();

		return task.getPorderDoValue() != null ? task.getPorderDoValue() : task.getPorterDo();
	}

	public final boolean confirm(String key, Object... params) throws InterruptedException {
		Task task = this.taskThread.getTask();
		task.request(new Confirm(key, text(key, params)));

		this.taskThread.requestIteration();

		return task.getPorderDoValue() != null ? Boolean.TRUE == task.getPorderDoValue() : false;
	}

	public final void error(String key, Object... params) {
		Task task = this.taskThread.getTask();
		task.error(new Message(key, text(key, params)));
	}

	public void info(int index, String key, Object... params) {
		Task task = this.taskThread.getTask();
		task.info(index, new Message(key, text(key, params)));
	}

	private String text(String key) {
		Task task = this.taskThread.getTask();
		ResourceBundle rb = ResourceBundle.getBundle("messages.properties",
				task.getContext().getLocale());

		String text = rb.getString(this.type + "." + key);

		if (text == null) {
			text = rb.getString(key);
		}

		return text;
	}

	private String text(String key, Object[] params) {
		return MessageFormat.format(text(key), params);
	}
}

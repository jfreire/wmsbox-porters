package com.wmsbox.porters.patron;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Confirm;
import com.wmsbox.porters.commons.interaction.InputInteger;
import com.wmsbox.porters.commons.interaction.InputString;
import com.wmsbox.porters.commons.interaction.Message;

public abstract class OperationController {

	private final OperationType type;
	private OperationThread operationThread;

	public OperationController(OperationType type) {
		this.type = type;
	}

	protected final OperationType getType() {
		return this.type;
	}

	protected final void init(OperationThread operationThread) {
		this.operationThread = operationThread;
	}

	/**
	 *
	 * @return La siguiente tarea o null.
	 * @throws InterruptedException
	 */
	public abstract OperationController process() throws InterruptedException;

	public final String inputString(String key)	throws InterruptedException {
		return inputString(key, null);
	}

	public final String inputString(String key, String defaultValue)
			throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		operation.request(new InputString(key, text(key), null));

		operation = this.operationThread.requestIteration();
		System.out.println("Response " + operation.getPorterDo() + " - " + operation.getPorderDoValue());

		return (String) operation.getPorderDoValue();
	}

	public final Integer inputInteger(String key) throws InterruptedException {
		return inputInteger(key, null);
	}

	public final Integer inputInteger(String key, Integer defaultValue)
			throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		operation.request(new InputInteger(key, text(key), defaultValue));

		operation = this.operationThread.requestIteration();
		System.out.println("ResponseInteger " + operation.getPorterDo() + " - " + operation.getPorderDoValue());

		return (Integer) operation.getPorderDoValue();
	}

	/**
	 *
	 * @param actionKeys pueden ser opciones o un input y opciones.
	 * @return Resultado de la entrada o el botón pulsado
	 * @throws InterruptedException
	 */
	public final Object choose(Action... actionKeys) throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		operation.request(actionKeys);

		operation = this.operationThread.requestIteration();

		return operation.getPorderDoValue() != null ? operation.getPorderDoValue()
				: operation.getPorterDo();
	}

	public final boolean confirm(String key, Object... params) throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		operation.request(new Confirm(key, text(key, params)));

		operation = this.operationThread.requestIteration();

		return operation.getPorderDoValue() != null ? Boolean.TRUE == operation.getPorderDoValue()
				: false;
	}

	public final void error(String key, Object... params) {
		Operation operation = this.operationThread.getOperation();
		operation.error(new Message(key, text(key, params)));
	}

	public void info(int index, String key, Object... params) {
		Operation operation = this.operationThread.getOperation();
		operation.info(index, new Message(key, text(key, params)));
	}

	private String text(String key) {
		Operation operation = this.operationThread.getOperation();
		ResourceBundle rb = ResourceBundle.getBundle("messages",
				operation.getContext().getLocale(), getClass().getClassLoader());

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

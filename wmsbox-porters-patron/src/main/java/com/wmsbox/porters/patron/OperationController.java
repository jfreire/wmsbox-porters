package com.wmsbox.porters.patron;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Button;
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

	public final String inputString(String key) throws InterruptedException {
		return (String) inputString(key, (String) null);
	}

	public final Object inputString(String key, OptionKey... options) throws InterruptedException {
		return inputString(key, null, options);
	}

	public final Object inputString(String key, String defaultValue, OptionKey... options)
			throws InterruptedException {
		return input(new InputString(key, text(key), null), options);

	}

	public final Object inputInteger(String key, OptionKey... options) throws InterruptedException {
		return inputInteger(key, null, options);
	}

	public final Integer inputInteger(String key, Integer defaultValue) throws InterruptedException {
		return (Integer) input(new InputInteger(key, text(key), null), null);
	}

	public final Object inputInteger(String key, Integer defaultValue, OptionKey... options)
			throws InterruptedException {
		return input(new InputInteger(key, text(key), null), options);
	}

	private Object input(Action action, OptionKey[] options) throws InterruptedException {
		Operation operation = this.operationThread.getOperation();

		if (options == null) {
			operation.request(action);
		} else {
			Action[] actions = new Action[options.length + 1];
			actions[0] = action;

			for (int i = 0; i < options.length; i++) {
				String optionKey = options[i].name();
				actions[i + 1] = new Button(optionKey, text(optionKey));
			}

			operation.request(actions);
		}

		operation = this.operationThread.requestIteration();

		Action porterDo = operation.getPorterDo();
		Object value = operation.getPorderDoValue();

		if (value != null) {
			return value;
		}

		for (OptionKey option : options) {
			if (porterDo.getKey().equals(option.name())) {
				return option;
			}
		}

		return null;
	}

	public final boolean confirm(String key, Object... params) throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		operation.request(new Confirm(key, text(key, params)));
		operation = this.operationThread.requestIteration();

		return operation.getPorderDoValue() != null ? Boolean.TRUE.equals(operation.getPorderDoValue())
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

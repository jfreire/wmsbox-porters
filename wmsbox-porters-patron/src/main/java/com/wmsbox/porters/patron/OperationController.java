package com.wmsbox.porters.patron;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.wmsbox.porters.commons.Operation;
import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Button;
import com.wmsbox.porters.commons.interaction.Confirm;
import com.wmsbox.porters.commons.interaction.Input;
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

	@SuppressWarnings("unchecked")
	public final <T> T input(String key, InputType<T> type) throws InterruptedException {
		return (T) inputOrChoice(key, type, null, (OptionKey[]) null);
	}

	@SuppressWarnings("unchecked")
	public final <T> T input(String key, InputType<T> type, T defaultValue)
			throws InterruptedException {
		return (T) inputOrChoice(key, type, defaultValue);
	}

	public final <T> Object inputOrChoice(String key, InputType<T> type, OptionKey... options)
			throws InterruptedException {
		return inputOrChoice(key, type, null, options);
	}

	public final <T> Object inputOrChoice(String key, InputType<T> type, T defaultValue,
			OptionKey... options) throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		Object result = null;
		String initialValue = defaultValue != null ? type.toString(defaultValue) : null;
		System.out.println("====> inputOrChoice " + key + " - " + result);
		while (result == null) {
			Object temporalResult = innerInputOrChoice(key, type, initialValue, options);

			if (temporalResult instanceof OptionKey) {
				result = temporalResult;
			} else {
				result = type.toType((String) temporalResult);

				if (result == null) {
					initialValue = operation.getPorderDoValue();
					error(type.name() + ".invalid", initialValue);
				}
			}
		}

		return result;
	}

	private final <T> Object innerInputOrChoice(String key, InputType<T> type, String initialValue,
			OptionKey[] options) throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		Input input = new Input(key, text(key), type.name(), type.getMode(),  initialValue);
System.out.println("====> innerInputOrChoice " + input + " - " + options);
		if (options == null || options.length == 0) {
			operation.request(input);
		} else {
			Action[] actions = new Action[options.length + 1];
			actions[0] = input;

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
			System.out.println("----> Value " + value);
			return value;
		}

		if (options != null && options.length > 0) {
			for (OptionKey option : options) {
				if (porterDo.getKey().equals(option.name())) {
					return option;
				}
			}
		}

		return null;
	}

	public final OptionKey choice(OptionKey... options) throws InterruptedException {
		Operation operation = this.operationThread.getOperation();
		Action[] actions = new Action[options.length];

		for (int i = 0; i < options.length; i++) {
			String optionKey = options[i].name();
			actions[i] = new Button(optionKey, text(optionKey));
		}

		operation.request(actions);
		operation = this.operationThread.requestIteration();

		Action porterDo = operation.getPorterDo();

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
		ResourceBundle rb = ResourceBundle.getBundle("messages", locale(),
				getClass().getClassLoader());

		String text;

		try {
			text = rb.getString(this.type + "." + key);
		} catch (MissingResourceException e) {
			try {
				text = rb.getString(key);
			} catch (MissingResourceException e2) {
				text = key;
			}
		}

		return text;
	}

	protected Locale locale() {
		Operation operation = this.operationThread.getOperation();

		return operation.getContext().getLocale();
	}

	public void completed(String key, Object... params) {
		Operation operation = this.operationThread.getOperation();
		operation.completed(new Message(key, text(key, params)));
	}

	public void canceled(String key, Object... params) {
		Operation operation = this.operationThread.getOperation();
		operation.cancelByPatron(new Message(key, text(key, params)));
	}

	private String text(String key, Object[] params) {
		return MessageFormat.format(text(key), params);
	}
}

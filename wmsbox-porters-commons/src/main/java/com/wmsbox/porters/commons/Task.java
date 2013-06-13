package com.wmsbox.porters.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Message;

public class Task extends Base {

	private static final long serialVersionUID = -5048824065756146177L;

	private final long id;
	private final List<Message> messages;
	private final Context context;
	private final TaskTypeCode type;
	private TaskState state;
	private Message error;
	private Action[] possibleActions;
	private Action porterDo;
	private Serializable porderDoValue;
	private long lastPorterActivityTime;

	public Task(long id, TaskTypeCode type, Context context) {
		this.id = id;
		this.type = type;
		this.context = context;
		this.state = TaskState.WAITING;
		this.messages = new ArrayList<Message>();
	}

	public long getId() {
		return this.id;
	}

	public Context getContext() {
		return this.context;
	}

	public long getLastPorterActivityTime() {
		return this.lastPorterActivityTime;
	}

	public TaskState getState() {
		return this.state;
	}

	public List<Message> getMessages() {
		return this.messages;
	}

	public Message getError() {
		return this.error;
	}

	public Action[] getPossibleActions() {
		return this.possibleActions;
	}

	public Action action(String actionKey) {
		for (Action action : this.possibleActions) {
			if (action.getKey().equals(actionKey)) {
				return action;
			}
		}

		return null;
	}

	public TaskTypeCode getType() {
		return type;
	}

	public Action getPorterDo() {
		return this.porterDo;
	}

	public Serializable getPorderDoValue() {
		return this.porderDoValue;
	}

	public void porterDo(Action action, Serializable value) {
		this.lastPorterActivityTime = System.currentTimeMillis();
		this.porterDo = action;
		this.porderDoValue = value;
	}

	public void reset() {
		this.porderDoValue = null;
		this.porterDo = null;
		this.error = null;
		this.possibleActions = null;
	}

	public void info(int index, Message message) {
		checkProcessingState();
		this.messages.add(message);
	}

	public void error(Message message) {
		checkProcessingState();
		this.error = message;
	}

	public void request(Action... possibleActions) {
		checkProcessingState();
		this.possibleActions = possibleActions;
	}

	public void cancelByPatron() {
		if (this.state.isLive()) {
			this.state = TaskState.CANCELED_BY_PATRON;
		} else {
			throw new IllegalStateException();
		}
	}

	public void cancelByPorter() {
		if (this.state.isLive()) {
			this.state = TaskState.CANCELED_BY_PORTER;
		} else {
			throw new IllegalStateException();
		}
	}

	public void completed() {
		checkProcessingState();
		this.state = TaskState.COMPLETED;
	}

	public void goToProcess() {
		if (this.state == TaskState.WAITING) {
			this.state = TaskState.PROCESSING;
		} else {
			throw new IllegalStateException();
		}
	}

	private void checkProcessingState() {
		if (this.state != TaskState.PROCESSING) {
			throw new IllegalStateException();
		}
	}

	@Override
	public String toString() {
		return "Task[" + this.id + ", " + this.type + ", " + Arrays.toString(this.possibleActions)
				+ ", " + this.error + ", " + this.messages + "]";
	}
}

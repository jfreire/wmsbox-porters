package com.wmsbox.porters.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wmsbox.porters.commons.interaction.Action;
import com.wmsbox.porters.commons.interaction.Message;

public class Operation extends Base {

	private static final long serialVersionUID = -5048824065756146177L;

	private final long id;
	private final List<Message> messages;
	private final Context context;
	private final OperationType type;
	private OperationState state;
	private Message error;
	private Message endMessage;
	private Message previousEndMessage;
	private Action[] possibleActions;
	private Action porterDo;
	private String porderDoValue;
	private long lastPorterActivityTime;
	private final Set<String> tags = new HashSet<String>();

	public Operation(long id, OperationType type, Context context) {
		this.id = id;
		this.type = type;
		this.context = context;
		this.state = OperationState.WAITING;
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

	public OperationState getState() {
		return this.state;
	}

	public List<Message> getMessages() {
		return this.messages;
	}

	public Message getError() {
		return this.error;
	}

	public Message getEndMessage() {
		return this.endMessage;
	}

	public Message getPreviousEndMessage() {
		return this.previousEndMessage;
	}

	public void setPreviousEndMessage(Message previousEndMessage) {
		this.previousEndMessage = previousEndMessage;
	}

	public Action[] getPossibleActions() {
		return this.possibleActions;
	}

	public OperationType getType() {
		return type;
	}

	public Action getPorterDo() {
		return this.porterDo;
	}

	public String getPorderDoValue() {
		return this.porderDoValue;
	}

	public void porterDo(Action action, String value) {
		this.lastPorterActivityTime = System.currentTimeMillis();
		this.porterDo = action;
		this.porderDoValue = value;
	}

	public void reset() {
		this.porderDoValue = null;
		this.porterDo = null;
		this.error = null;
		this.possibleActions = null;
		this.previousEndMessage = null;
	}

	public void info(int index, Message message) {
		checkProcessingState();
		
		if (this.messages.size() > index) {
			this.messages.set(index, message);
		} else {
			//TODO revisar
			this.messages.add(message);
		}
	}

	public void error(Message message) {
		checkProcessingState();
		this.error = message;
	}

	public void request(Action... possibleActions) {
		checkProcessingState();
		this.possibleActions = possibleActions;
	}

	public void cancelByPatron(Message message) {
		if (this.state.isLive()) {
			this.endMessage = message;
			this.state = OperationState.CANCELED_BY_PATRON;
		} else {
			throw new IllegalStateException();
		}
	}

	public void cancelByPorter(Message message) {
		if (this.state.isLive()) {
			this.endMessage = message;
			this.state = OperationState.CANCELED_BY_PORTER;
		} else {
			throw new IllegalStateException();
		}
	}

	public void completed(Message message) {
		checkProcessingState();
		this.endMessage = message;
		this.state = OperationState.COMPLETED;
	}

	public void goToProcess() {
		if (this.state == OperationState.WAITING) {
			this.state = OperationState.PROCESSING;
		} else {
			throw new IllegalStateException();
		}
	}

	private void checkProcessingState() {
		if (this.state != OperationState.PROCESSING) {
			throw new IllegalStateException();
		}
	}
	
	public void addTag(String tag) {
		this.tags.add(tag);
	}
	
	public void removeTag(String tag) {
		this.tags.remove(tag);
	}
	
	public boolean hasTag(String tag) {
		return this.tags.contains(tag);
	}
	
	public Set<String> getTags() {
		return Collections.unmodifiableSet(this.tags);
	}

	@Override
	public String toString() {
		return "Task[" + this.id + ", " + this.type + ", " + Arrays.toString(this.possibleActions)
				+ ", " + this.error + ", " + this.messages + ", " + this.porterDo + ", "
				+ this.porderDoValue + "]";
	}
}

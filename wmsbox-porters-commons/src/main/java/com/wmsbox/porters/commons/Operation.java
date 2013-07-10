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
	private final List<Message> info;
	private final Context context;
	private final OperationType type;
	private OperationState state;
	private Message message;
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
		this.info = new ArrayList<Message>();
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
		return this.info;
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

	public Message getMessage() {
		return this.message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void reset() {
		this.porderDoValue = null;
		this.porterDo = null;
		this.possibleActions = null;
		this.message = null;
	}

	public void info(int index, Message message) {
		checkProcessingState();

		if (this.info.size() > index) {
			this.info.set(index, message);
		} else {
			//TODO revisar
			this.info.add(message);
		}
	}

	public void error(Message message) {
		checkProcessingState();
		this.message = message;
	}

	public void request(Action... possibleActions) {
		checkProcessingState();
		this.possibleActions = possibleActions;
	}

	public void cancelByPatron(Message message) {
		if (this.state.isLive()) {
			this.message = message;
			this.state = OperationState.CANCELED_BY_PATRON;
		} else {
			throw new IllegalStateException("CancelByPatron " + this);
		}
	}

	public void cancelByPorter(Message message) {
		if (this.state.isLive()) {
			this.message = message;
			this.state = OperationState.CANCELED_BY_PORTER;
		} else {
			throw new IllegalStateException("CancelByPorter " + this);
		}
	}

	public void completed(Message message) {
		checkProcessingState();
		this.message = message;
		this.state = OperationState.COMPLETED;
	}

	public void goToProcess() {
		if (this.state == OperationState.WAITING) {
			this.state = OperationState.PROCESSING;
		} else {
			throw new IllegalStateException("GoToProcess " + this);
		}
	}

	private void checkProcessingState() {
		if (this.state != OperationState.PROCESSING) {
			throw new IllegalStateException("CheckingProcessingState " + this);
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
		return "Operation[" + this.id + ", " + this.type + ", " + this.state + ", "
				+ Arrays.toString(this.possibleActions)
				+ ", " + this.message + ", " + this.info + ", " + this.porterDo + ", "
				+ this.porderDoValue + "]";
	}
}

package com.wmsbox.porters.commons;

public enum OperationState {

	WAITING(true),
	PROCESSING(true),
	COMPLETED(false),
	CANCELED_BY_PORTER(false),
	CANCELED_BY_PATRON(false);

	private final boolean live;

	private OperationState(boolean live) {
		this.live = live;
	}

	public boolean isLive() {
		return this.live;
	}
}

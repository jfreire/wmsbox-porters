package com.wmsbox.porters.sample;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.wmsbox.porters.commons.TaskTypeCode;
import com.wmsbox.porters.patron.OverseerConnector;
import com.wmsbox.porters.patron.Patron;
import com.wmsbox.porters.patron.TaskController;

public class SamplePatron implements Patron {

	public String getKey() {
		return "Sample";
	}

	public List<TaskTypeCode> getTaskTypes() {
		List<TaskTypeCode> types = new ArrayList<TaskTypeCode>();

		types.add(SplitterTaskController.CODE);

		return types;
	}

	public TaskController porterRequestTask(String code) {

		return new SplitterTaskController(code);
	}

	public TaskController porterRequestTask(TaskTypeCode type) {

		return new SplitterTaskController();
	}

	public static void main(String[] args) throws RemoteException, NotBoundException {
		OverseerConnector connector = new OverseerConnector();
		connector.setHost("localhost");
		connector.setPort(8888);
		connector.start(new SamplePatron());
	}
}

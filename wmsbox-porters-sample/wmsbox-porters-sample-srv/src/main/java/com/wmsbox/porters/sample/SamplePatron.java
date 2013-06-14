package com.wmsbox.porters.sample;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.patron.OverseerConnector;
import com.wmsbox.porters.patron.Patron;
import com.wmsbox.porters.patron.OperationController;

public class SamplePatron implements Patron {

	public String getKey() {
		return "Sample";
	}

	public List<OperationType> getOperationTypes() {
		List<OperationType> types = new ArrayList<OperationType>();

		types.add(SplitterOperationController.CODE);

		return types;
	}

	public OperationController porterRequestOperation(String code) {
		return new SplitterOperationController(code);
	}

	public OperationController porterRequestOperation(OperationType type) {
		return new SplitterOperationController();
	}

	public static void main(String[] args) throws RemoteException, NotBoundException {
		OverseerConnector connector = new OverseerConnector();
		connector.setHost("localhost");
		connector.setPort(8888);
		connector.start(new SamplePatron());
	}
}

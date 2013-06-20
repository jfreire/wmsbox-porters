package com.wmsbox.porters.sample;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.patron.OperationController;
import com.wmsbox.porters.patron.OverseerConnector;
import com.wmsbox.porters.patron.Patron;

public class SamplePatron implements Patron {

	public String getKey() {
		return "Sample";
	}

	public List<OperationType> getOperationTypes() {
		List<OperationType> types = new ArrayList<OperationType>();

		types.add(SplitterOperationController.CODE);
		types.add(RejectionOperationController.CODE);

		return types;
	}

	public OperationController porterRequestOperation(String code) {
		Container container = ContainerRepo.INSTANCE.findContainer(code);

		if (container != null) {
			if (container.getPosition().equals(ContainerRepo.SPLITTER)) {
				return new SplitterOperationController(code);
			}

			return new RejectionOperationController(code);
		} else {
			return new IdentifyOperationController(code);
		}
	}

	public OperationController porterRequestOperation(OperationType type) {
		if (type.equals(SplitterOperationController.CODE)) {
			return new SplitterOperationController();
		} else {
			return new RejectionOperationController();
		}
	}

	public static void main(String[] args) throws RemoteException, NotBoundException {
		OverseerConnector connector = new OverseerConnector();
		connector.setHost("localhost");
		connector.setPort(8888);
		connector.start(new SamplePatron());
	}

	public String getResourcesFile() {
		return "messages";
	}
}

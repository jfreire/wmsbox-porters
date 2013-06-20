package com.wmsbox.porters.patron;

import java.util.List;

import com.wmsbox.porters.commons.OperationType;

public interface Patron {

	String getKey();

	String getResourcesFile();
	
	List<OperationType> getOperationTypes();

	OperationController porterRequestOperation(String code);

	OperationController porterRequestOperation(OperationType type);
}

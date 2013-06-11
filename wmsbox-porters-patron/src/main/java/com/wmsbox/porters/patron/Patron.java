package com.wmsbox.porters.patron;

import java.util.List;

import com.wmsbox.porters.commons.TaskTypeCode;

public interface Patron {

	String getKey();

	List<TaskTypeCode> getTaskTypes();

	TaskController porterRequestTask(String code);

	TaskController porterRequestTask(TaskTypeCode type);
}

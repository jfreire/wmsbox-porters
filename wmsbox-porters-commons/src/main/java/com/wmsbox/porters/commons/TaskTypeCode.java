package com.wmsbox.porters.commons;

import com.wmsbox.codes.Name;
import com.wmsbox.codes.Size;
import com.wmsbox.codes.string.StringCode;


public class TaskTypeCode extends StringCode<TaskTypeCode> {

	private static final long serialVersionUID = 7470255836771314636L;

	private final String patronKey;

	public TaskTypeCode(String code, @Name('A') @Size(3) String patronKey,
			@Name('B') @Size(3) String typeKey) {
		super(code);
		this.patronKey = patronKey;
	}

	public String getPatronKey() {
		return this.patronKey;
	}
}

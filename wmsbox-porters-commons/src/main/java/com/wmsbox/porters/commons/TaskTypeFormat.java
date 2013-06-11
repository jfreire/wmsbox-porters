package com.wmsbox.porters.commons;

import com.wmsbox.codes.metainfo.FieldsExtractor;
import com.wmsbox.codes.string.StringFormat;

public class TaskTypeFormat extends StringFormat<TaskTypeCode> {

	public static final TaskTypeFormat INSTANCE = new TaskTypeFormat();

	private TaskTypeFormat() {
		super("TaskType", FieldsExtractor.extract(TaskTypeCode.class), "AAA'.'BBB");
	}

	@Override
	protected TaskTypeCode create(String toString, Object[] values) {
		return new TaskTypeCode(toString, (String) values[0], null);
	}

	public final TaskTypeCode create(String patronKey, String opKey) {
		return create(new Object[] { patronKey, opKey } );
	}
}

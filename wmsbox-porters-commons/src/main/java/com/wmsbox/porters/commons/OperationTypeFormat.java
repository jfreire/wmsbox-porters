package com.wmsbox.porters.commons;

import com.wmsbox.codes.metainfo.FieldsExtractor;
import com.wmsbox.codes.string.StringFormat;

public final class OperationTypeFormat extends StringFormat<OperationType> {

	public static final OperationTypeFormat INSTANCE = new OperationTypeFormat();

	private OperationTypeFormat() {
		super("TaskType", FieldsExtractor.extract(OperationType.class), "AAA'.'BBB");
	}

	@Override
	protected OperationType create(String toString, Object[] values) {
		return new OperationType(toString, (String) values[0], null);
	}

	public OperationType create(String patronKey, String opKey) {
		return create(new Object[] { patronKey, opKey } );
	}
}

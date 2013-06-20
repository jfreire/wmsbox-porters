package com.wmsbox.porters.sample;

import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OperationTypeFormat;
import com.wmsbox.porters.patron.OperationController;
import com.wmsbox.porters.patron.StringInputType;


public class IdentifyOperationController extends OperationController {

	public static final OperationType CODE = OperationTypeFormat.INSTANCE.create("ASR", "IDE");

	private final String label;

	public IdentifyOperationController(String label) {
		super(CODE);
		this.label = label;
	}

	@Override
	public OperationController process() throws InterruptedException {
		info(1, "info.label", this.label);

		String position = input("position", StringInputType.ANY);

		if (position != null) {
			info(2, "info.position", position);

			if (confirm("containerNotExists.confirmCreate", this.label)) {
				completed(null);

				return null;
			} else {
				completed(null);

				return null;
			}
		} else {
			return null;
		}
	}
}

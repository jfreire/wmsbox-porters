package com.wmsbox.porters.sample;

import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OperationTypeFormat;
import com.wmsbox.porters.patron.OperationController;
import com.wmsbox.porters.patron.StringInputType;


public class RejectionOperationController extends OperationController {

	public static final OperationType CODE = OperationTypeFormat.INSTANCE.create("ASR", "RECH");

	private String label;

	public RejectionOperationController() {
		super(CODE);
		this.label = null;
	}

	public RejectionOperationController(String label) {
		super(CODE);
		this.label = label;
	}

	@Override
	public OperationController process() throws InterruptedException {
		Container container = readContainer();
		int totalUnits = container.getUnits();
		info(1, "container.label", container.getLabel());
		info(2, "container.position", container.getPosition());
		info(3, "container.content", container.getSku(), totalUnits);

		readContainer(); //TODO

		return null;
	}

	private Container readContainer() throws InterruptedException {
		Container result = null;

		while (result == null) {
			String containerLabel;

			if (this.label != null) {
				containerLabel = this.label;
				this.label = null;
			} else {
				containerLabel = input("container", StringInputType.DIGITS);
			}

			Container container = ContainerRepo.INSTANCE.findContainer(containerLabel);

			if (container == null) {
				if (confirm("container.notFound", containerLabel)) {
					//TODO
				} else {
					//TODO
				}
			}

			if (container.getPosition().equals(ContainerRepo.RECH)) {
				//TODO si no es el primero de la barra??
				result = container;
			} else if (confirm("container.confirmInRech", containerLabel, container.getPosition())) {
				container.setPosition(ContainerRepo.RECH);
				result = container;
			} else {
				//TODO
			}
		}

		return result;
	}
}

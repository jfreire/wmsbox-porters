package com.wmsbox.porters.sample;

import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OperationTypeFormat;
import com.wmsbox.porters.patron.OperationController;
import com.wmsbox.porters.patron.OptionKey;


public class SplitterOperationController extends OperationController {

	public static final OperationType CODE = OperationTypeFormat.INSTANCE.create("ASR", "SPL");

	private static enum Option implements OptionKey {
		LAST;
	}

	private String sourceLabel;

	public SplitterOperationController() {
		super(CODE);
		this.sourceLabel = null;
	}

	public SplitterOperationController(String sourceLabel) {
		super(CODE);
		this.sourceLabel = sourceLabel;
	}

	@Override
	public OperationController process() throws InterruptedException {
		Container container = readContainerSource();
		int totalUnits = container.getUnits();
		info(1, "container.label", container.getLabel());
		info(2, "container.position", container.getPosition());
		info(3, "container.content", container.getSku(), totalUnits);

		int units = readUnits(totalUnits, ContainerRepo.INSTANCE.garmentsPerBar(container.getSku()));

		String targetLabel = readTargetLabel();

		if (targetLabel != null) {
			ContainerRepo.INSTANCE.transfer(container, targetLabel, units);

			return new SplitterOperationController(targetLabel);
		}

		return null;
	}

	private Container readContainerSource() throws InterruptedException {
		Container result = null;

		while (result == null) {
			String containerLabel;
			
			if (this.sourceLabel != null) {
				containerLabel = this.sourceLabel;
				this.sourceLabel = null;
			} else {
				containerLabel = inputString("container");
			}
			
			Container container = ContainerRepo.INSTANCE.findContainer(containerLabel);

			if (container == null) {
				error("container.notFound", containerLabel);
			}
			
			if (container.getPosition().equals(ContainerRepo.SPLITTER)) {
				//TODO si no es el primero de la barra??
				result = container;
			} else if (confirm("container.confirmInBar", containerLabel, container.getPosition())) {
				container.setPosition(ContainerRepo.SPLITTER);
				result = container;
			} else {
				//TODO
			}
		}

		return result;
	}

	private int readUnits(int totalUnits, int garmentsPerMeter) throws InterruptedException {
		Integer units = null;
		int defaultUnits = Math.min(totalUnits, garmentsPerMeter);

		while (units == null) {
			int enteredUnits = inputInteger("units", defaultUnits);

			if (enteredUnits > garmentsPerMeter * 2) {
				if (confirm("units.tooMuch", enteredUnits)) {
					info(3, "units.info", enteredUnits);
					units = enteredUnits;
				}
			} else {
				units = enteredUnits;
			}
		}

		return units;
	}

	public String readTargetLabel() throws InterruptedException {
		String label = null;

		while (label == null) {
			Object result = inputString("target", Option.LAST);

			if (result == Option.LAST) {
				return null;
			}

			String currentLabel = (String) result;

			if (ContainerRepo.INSTANCE.findContainer(currentLabel) != null) {
				error("target.exists", currentLabel);
			} else {
				label = currentLabel;
			}
		}

		return label;
	}
}

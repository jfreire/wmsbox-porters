package com.wmsbox.porters.sample;

import com.wmsbox.porters.commons.TaskTypeCode;
import com.wmsbox.porters.commons.TaskTypeFormat;
import com.wmsbox.porters.patron.TaskController;


public class SplitterTaskController extends TaskController {

	public static final TaskTypeCode CODE = TaskTypeFormat.INSTANCE.create("ASR", "SPL");

	private final String originLabel;

	public SplitterTaskController() {
		super(CODE);
		this.originLabel = null;
	}

	public SplitterTaskController(String originLabel) {
		super(CODE);
		this.originLabel = originLabel;
	}

	@Override
	public TaskController process() throws InterruptedException {
		Container container = readContainerOrigin();
		int totalUnits = container.getUnits();
		info(1, "container.content", container.getSku(), totalUnits);

		int units = readUnits(totalUnits, garmentsPerBar(container.getSku()));

		String targetLabel = readTargetLabel();
		transfer(container, targetLabel, units);

		return new SplitterTaskController(originLabel);
	}

	private Container readContainerOrigin() throws InterruptedException {
		Container container = null;

		while (container == null) {
			String containerLabel = input(String.class, "container");
			container = findContainer(containerLabel);

			if (container == null) {
				error("container.notFound", containerLabel);
			}
		}

		return container;
	}

	private int readUnits(int totalUnits, int garmentsPerMeter) throws InterruptedException {
		Integer units = null;
		int defaultUnits = Math.min(totalUnits, garmentsPerMeter);

		while (units == null) {
			int enteredUnits = input(Integer.class, "units", defaultUnits);

			if (enteredUnits > garmentsPerMeter * 2) {
				if (confirm("tooMuch.units", enteredUnits)) {
					units = enteredUnits;
				}
			}
		}

		return units;
	}

	public String readTargetLabel() throws InterruptedException {
		String label = null;

		while (label != null) {
			String currentLabel = input(String.class, "target");

			if (existsContainer(currentLabel)) {
				error("exists", currentLabel);
			} else {
				label = currentLabel;
			}
		}

		return label;
	}

	private boolean existsContainer(String label) {
		return true;
	}

	private Container findContainer(String label) {
		return null;
	}

	private int garmentsPerBar(long sku) {
		return 0;
	}

	private void transfer(Container container, String targetLabel, int units) {
		//Nada
	}
}

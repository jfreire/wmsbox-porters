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
		Container container = readContainerSource();
		int totalUnits = container.getUnits();
		info(1, "container.content", container.getSku(), totalUnits);

		int units = readUnits(totalUnits, garmentsPerBar(container.getSku()));

		String targetLabel = readTargetLabel();
		transfer(container, targetLabel, units);

		return new SplitterTaskController(originLabel);
	}

	private Container readContainerSource() throws InterruptedException {
		Container container = null;

		while (container == null) {
			String containerLabel = inputString("container");
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
			int enteredUnits = inputInteger("units", defaultUnits);

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
			String currentLabel = inputString("target");

			if (existsContainer(currentLabel)) {
				error("exists", currentLabel);
			} else {
				label = currentLabel;
			}
		}

		return label;
	}

	private boolean existsContainer(String label) {
		try {
			Long.parseLong(label);

			return !label.endsWith("8") && !label.endsWith("9");
		} catch (Exception e) {
			return false;
		}
	}

	private Container findContainer(String label) {
		try {
			long code = Long.parseLong(label);


			if (label.endsWith("8") || label.endsWith("9")) {
				return new Container(label, 12341231200l + (int) ((code / 1000) % 100),
						(int) ((code / 10) % 100));
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private int garmentsPerBar(long sku) {
		return 50;
	}

	private void transfer(Container container, String targetLabel, int units) {
		//Nada
	}
}

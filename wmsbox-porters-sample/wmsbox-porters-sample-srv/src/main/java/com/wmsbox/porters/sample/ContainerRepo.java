package com.wmsbox.porters.sample;

import java.util.HashMap;
import java.util.Map;

public class ContainerRepo {
	
	public static final String RECH = "RECH";
	public static final String SPLITTER = "SPLITTER";

	public static ContainerRepo INSTANCE = new ContainerRepo();

	private final Map<String, Container> containers = new HashMap<String, Container>();


	public void transfer(Container container, String targetLabel, int units) {
		this.containers.put(targetLabel, new Container(targetLabel, container.getPosition(), 
				container.getSku(),	container.getUnits() - units));

		System.out.println("Transfer completed " + container + " - " + targetLabel + " - " + units);
	}

	public int garmentsPerBar(long sku) {
		return 30;
	}

	public Container findContainer(String label) {
		Container container = this.containers.get(label);

		if (container != null) {
			return container;
		}

		try {
			long code = Long.parseLong(label);

			if (label.endsWith("8") || label.endsWith("9")) {
				return new Container(label, label.endsWith("8") ? RECH : SPLITTER, 
						12341231200l + (int) ((code / 1000) % 100),	(int) ((code / 10) % 100));
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}
}

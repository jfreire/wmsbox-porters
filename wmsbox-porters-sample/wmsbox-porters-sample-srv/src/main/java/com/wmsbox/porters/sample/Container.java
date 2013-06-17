package com.wmsbox.porters.sample;

public class Container {

	private final String label;
	private String position;
	private final long sku;
	private int units;
	
	public Container(String label, String position, long sku, int units) {
		this.label = label;
		this.position = position;
		this.sku = sku;
		this.units = units;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}

	public String getLabel() {
		return label;
	}

	public long getSku() {
		return sku;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}

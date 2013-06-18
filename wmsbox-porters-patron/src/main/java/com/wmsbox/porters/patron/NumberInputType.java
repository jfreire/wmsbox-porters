package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.interaction.InputMode;

public enum NumberInputType implements InputType<Integer> {
	NATURAL {
		public Integer convert(String value) {
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				return null;
			}
		}
	},
	POSITIVE_NOT_ZERO {
		public Integer convert(String value) {
			try {
				int num = Integer.parseInt(value);

				return num > 0 ? num : null;
			} catch (NumberFormatException e) {
				return null;
			}
		}
	};

	public String convert(Integer value) {
		return value.toString();
	}

	public InputMode getMode() {
		return InputMode.NUMBER;
	}
}

package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.interaction.InputMode;

public enum NumberInputType implements InputType<Integer> {
	NATURAL {
		public Integer toType(String value) {
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				return null;
			}
		}
	},
	POSITIVE_NOT_ZERO {
		public Integer toType(String value) {
			try {
				int num = Integer.parseInt(value);

				return num > 0 ? num : null;
			} catch (NumberFormatException e) {
				return null;
			}
		}
	};

	public String toString(Integer value) {
		return value.toString();
	}

	public InputMode getMode() {
		return InputMode.NUMBER;
	}
}

package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.interaction.InputMode;

public enum StringInputType implements InputType<String> {
	ANY {
		public String toType(String value) {
			return value;
		}
	},
	DIGITS {
		public String toType(String value) {
			for (int i = 0; i < value.length(); i++) {
				if (!Character.isDigit(value.charAt(i))) {
					return null;
				}
			}

			return value;
		}
	};

	public String toString(String value) {
		return value;
	}

	public InputMode getMode() {
		return InputMode.TEXT;
	}
}

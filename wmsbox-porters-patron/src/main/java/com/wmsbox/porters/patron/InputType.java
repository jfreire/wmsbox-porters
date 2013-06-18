package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.interaction.InputMode;

public interface InputType<T> {

	String name();

	InputMode getMode();

	T convert(String value);

	String convert(T value);
}

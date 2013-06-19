package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.interaction.InputMode;

public interface InputType<T> {

	String name();

	InputMode getMode();

	T toType(String value);

	String toString(T value);
}

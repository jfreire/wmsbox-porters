package com.wmsbox.porters.patron;

import com.wmsbox.porters.commons.interaction.InputMode;

public interface InputType<T> {

	String name();

	InputMode getMode();

	T toType(String value);

	String toString(T value);
	
	/**
	 * Se usa para obtener el formato de un valor.
	 * 
	 * @param value
	 * 
	 * @return Nulo o un string que identifique el formato a la hora de presentar al usuario el valor.
	 */
	String getFormat(T value);
}

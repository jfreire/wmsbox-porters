package com.wmsbox.porters.sample;

import com.wmsbox.porters.commons.OperationType;
import com.wmsbox.porters.commons.OperationTypeFormat;
import com.wmsbox.porters.patron.OperationController;
import com.wmsbox.porters.patron.OptionKey;


public class SplitterOperationController extends OperationController {

	public static final OperationType CODE = OperationTypeFormat.INSTANCE.create("ASR", "SPL");

	private static enum Option implements OptionKey {
		LAST;
	}

	private String sourceLabel;

	public SplitterOperationController() {
		super(CODE);
		this.sourceLabel = null;
	}

	public SplitterOperationController(String sourceLabel) {
		super(CODE);
		this.sourceLabel = sourceLabel;
	}

	@Override
	public OperationController process() throws InterruptedException {
		Container container = readContainerSource();

		int totalUnits = container.getUnits();
		info(1, "container.label", container.getLabel());
		info(2, "container.position", container.getPosition());
		info(3, "container.content", container.getSku(), totalUnits);
		//TODO mostrar descripción del articulo.

		int garmentsPerBar = ContainerRepo.INSTANCE.garmentsPerBar(container.getSku());
		info (4, "container.estimatedGarments", garmentsPerBar);

		//Si pocas unidades y siguiente mismo sku pedir escanear y retirar siguiente matricula => TCON


		int units = readUnits(totalUnits, garmentsPerBar);

		String targetLabel = readTargetLabel();

		if (targetLabel != null) {
			ContainerRepo.INSTANCE.transfer(container, targetLabel, units);

			return new SplitterOperationController(targetLabel);
		}

		return null;
	}

	private Container readContainerSource() throws InterruptedException {
		Container result = null;

		while (result == null) {
			String containerLabel;

			if (this.sourceLabel != null) {
				containerLabel = this.sourceLabel;
				this.sourceLabel = null;
			} else {
				containerLabel = inputString("container");
			}

			//findContaienr solicitará SICR si hace falta y esperar 0's o el tiempo configurado.
			Container container = ContainerRepo.INSTANCE.findContainer(containerLabel);

			if (container == null) {
				error("container.notFound", containerLabel);

				if (confirm("container.confirmCreation", containerLabel)) {
					//TODO Solicitar sku y unidades?
					//container = new Container(containerLabel, ContainerRepo.SPLITTER, sku, units);
				}
			} else if (container.getPosition().equals(ContainerRepo.SPLITTER)) {
				if (true) { //Primero de la barra?
					result = container;
				} else {
					//TODO solicitar barra,

					if (true) { //Si barra es la esperada.
						result = container; //Mover contenedores más antiguas a null.
					} else {
						// No mover contenedores de la barra a null.
						// Actualizar posición ??
						result = container;
					}
				}
			} else if (confirm("container.confirmInBar", containerLabel, container.getPosition())) {
				container.setPosition(ContainerRepo.SPLITTER);

				//TODO solicitar barra,
				// No mover contenedores de la barra a null.
				// Actualizar posición ??
				result = container;
			} else {
				//TODO
			}
		}

		return result;
	}

	private int readUnits(int totalUnits, int garmentsPerMeter) throws InterruptedException {
		Integer units = null;

		int defaultUnits = Math.min(totalUnits, garmentsPerMeter);

		while (units == null) {
			//TODO opción de contenido invalido => Cancelar tarea actual e invocar la de modificación de pusto de rechazo
			int enteredUnits = inputInteger("units", defaultUnits);

			if (enteredUnits > garmentsPerMeter * 2) { //TODO margen configurable
				if (confirm("units.tooMuch", enteredUnits)) {
					info(4, "units.info", enteredUnits);
					units = enteredUnits;
				}
			} else {
				units = enteredUnits;
			}
		}

		return units;
	}

	public String readTargetLabel() throws InterruptedException {
		String label = null;

		while (label == null) {
			Object result = inputString("target", Option.LAST);

			if (result == Option.LAST) {
				return null;
			}

			String currentLabel = (String) result;

			if (ContainerRepo.INSTANCE.findContainer(currentLabel) != null) {
				error("target.exists", currentLabel);
			} else {
				label = currentLabel;
			}
		}

		return label;
	}
}

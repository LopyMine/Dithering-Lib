package net.lopymine.dl.dithering;

import lombok.*;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringDataBuffer;

@Getter
@Setter
@SuppressWarnings("unused")
public class DitheringData {

	private float far;
	private float near;
	private float minValue;
	private float fixedValue;
	private float pixelSize;

	private static final DitheringData INSTANCE = new DitheringData();

	public static DitheringData getInstance() {
		return INSTANCE;
	}

	public void push() {
		VanillaDitheringDataBuffer.update();
	}

}

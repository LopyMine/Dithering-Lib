package net.lopymine.dl.dithering;

import lombok.*;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringDataBuffer;
import net.lopymine.dl.thing.RenderingMarker;

@Getter
@Setter
@SuppressWarnings("unused")
public class DitheringData {

	public static final ThreadLocal<DitheringData> CURRENT_DITHERING_DATA = ThreadLocal.withInitial(() -> null);

	private float far = 0.0F;
	private float near = 0.0F;
	private float minValue = 0.0F;
	private float fixedValue = 1.0F;
	private float pixelSize = 10.0F;

	public void push() {
		CURRENT_DITHERING_DATA.set(this);
	}

}

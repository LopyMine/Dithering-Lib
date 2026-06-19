package net.lopymine.dl.dithering;

import net.lopymine.dl.utils.DitheringMarker;
import net.minecraft.client.renderer.SubmitNodeStorage;

public class DitheringRenderManager {

	public static SubmitNodeStorage createDitheringStorage() {
		SubmitNodeStorage storage = new SubmitNodeStorage();
		((DitheringMarker) storage).ditheringLib$setDithering(true);
		return storage;
	}
}

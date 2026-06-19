package net.lopymine.dl.utils;

import net.minecraft.client.renderer.SubmitNodeStorage;

public interface DitheringMarker {

	void ditheringLib$setDithering(boolean bl);

	boolean ditheringLib$isDithering();

	SubmitNodeStorage ditheringLib$getDitheringStorage();

}

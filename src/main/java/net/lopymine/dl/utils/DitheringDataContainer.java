package net.lopymine.dl.utils;

import net.lopymine.dl.dithering.DitheringData;
import org.jetbrains.annotations.Nullable;

public interface DitheringDataContainer {

	void ditheringLib$setData(@Nullable DitheringData data);

	@Nullable DitheringData ditheringLib$getData();

}

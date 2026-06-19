package net.lopymine.dl.mixin.render;

import net.lopymine.dl.dithering.DitheringData;
import net.lopymine.dl.utils.DitheringDataContainer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin implements DitheringDataContainer {

	@Unique
	@Nullable
	private DitheringData data;

	@Override
	public void ditheringLib$setData(@Nullable DitheringData data) {
		this.data = data;
	}

	@Override
	public @Nullable DitheringData ditheringLib$getData() {
		return this.data;
	}
}

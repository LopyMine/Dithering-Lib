package net.lopymine.dl.mixin.pipeline;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.lopymine.dl.utils.DitheringMarker;
import org.spongepowered.asm.mixin.*;

@Mixin(RenderPipeline.class)
public class RenderPipelineMixin implements DitheringMarker {

	@Unique
	private boolean ditheringLib$bl;

	@Override
	public void ditheringLib$setDithering(boolean bl) {
		this.ditheringLib$bl = bl;
	}

	@Override
	public boolean ditheringLib$isDithering() {
		return this.ditheringLib$bl;
	}
}

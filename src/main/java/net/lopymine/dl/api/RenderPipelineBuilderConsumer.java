package net.lopymine.dl.api;

import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import net.lopymine.dl.mixin.pipeline.RenderPipelineBuilderAccessor;

public interface RenderPipelineBuilderConsumer {

	void accept(Builder ditheringBuilder, RenderPipelineBuilderAccessor accessor);

}

package net.lopymine.dl.dithering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import java.util.*;
import java.util.Map.Entry;
import net.lopymine.dl.dithering.vanilla.*;

public class DitheringRenderPipelineManager {

	private static final Map<RenderPipeline, Builder> MAP = new HashMap<>();

	public static void savePipelineBuilder(RenderPipeline pipeline, Builder builder) {
		MAP.put(pipeline, builder);
	}

	public static void registerAndClear() {
		for (Entry<RenderPipeline, Builder> entry : MAP.entrySet()) {
			RenderPipeline pipeline = entry.getKey();
			Builder builder = entry.getValue();
			if (VanillaDitheringTargets.isTarget(pipeline.getFragmentShader())) {
				VanillaDitheringShaderManager.createDitheringPipeline(pipeline, builder);
			}
		}
		MAP.clear();
	}
}

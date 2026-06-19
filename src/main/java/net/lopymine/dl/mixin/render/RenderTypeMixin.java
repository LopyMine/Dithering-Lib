package net.lopymine.dl.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.compat.IrisAPI;
import net.lopymine.dl.dithering.vanilla.*;
import net.lopymine.dl.thing.RenderingMarker;
import net.lopymine.dl.utils.IrisDitheringMarker;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public class RenderTypeMixin {

	@Unique
	private boolean ditheringLib$bl2;
	@Unique
	@Nullable
	private RenderPipeline ditheringLib$lastRenderPipeline = null;
	@Unique
	@Nullable
	private RenderPipeline ditheringLib$lastDitheringRenderPipeline = null;

	@WrapOperation(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V"), method = "draw")
	private void swapRenderPipeline(RenderPass instance, RenderPipeline pipeline, Operation<Void> original) {
		boolean bl = IrisAPI.isShaderPackInUse();
		boolean ditheringEnabled = RenderingMarker.API_DITHERING_RENDERING.get().isEnabled();

		((IrisDitheringMarker) pipeline).ditheringLib$setDithering(ditheringEnabled && bl);
		if (!ditheringEnabled || !DitheringLibClient.isEnabled()) {
			this.ditheringLib$bl2 = false;
			original.call(instance, pipeline);
			return;
		}
		if (bl) {
			this.ditheringLib$bl2 = false;
			original.call(instance, pipeline);
			return;
		}
		if (this.ditheringLib$lastRenderPipeline == pipeline) {
			original.call(instance, this.ditheringLib$lastDitheringRenderPipeline);
			this.ditheringLib$bl2 = true;
			return;
		}
		RenderPipeline ditheringPipeline = VanillaDitheringShaderManager.getDitheringPipeline(pipeline);
		if (ditheringPipeline != null) {
			this.ditheringLib$lastRenderPipeline          = pipeline;
			this.ditheringLib$lastDitheringRenderPipeline = ditheringPipeline;
			original.call(instance, ditheringPipeline);
			this.ditheringLib$bl2 = true;
			return;
		}
		original.call(instance, pipeline);
		this.ditheringLib$bl2 = false;
	}

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderPass;setUniform(Ljava/lang/String;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
			),
			method = "draw"
	)
	private void bindDitheringDataUniform(CallbackInfo ci, @Local RenderPass pass) {
		if (!DitheringLibClient.isEnabled()) {
			return;
		}
		if (!this.ditheringLib$bl2) {
			return;
		}
		pass.setUniform("DitheringLibData", VanillaDitheringDataBuffer.getBuffer());
	}
}

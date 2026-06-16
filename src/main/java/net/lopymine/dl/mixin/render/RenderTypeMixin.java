package net.lopymine.dl.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.compat.IrisAPI;
import net.lopymine.dl.dithering.vanilla.*;
import net.lopymine.dl.utils.DitheringMarker;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public class RenderTypeMixin implements DitheringMarker {

	@Unique
	private boolean ditheringLib$bl;
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
		((DitheringMarker) pipeline).ditheringLib$setDithering(this.ditheringLib$bl);
		if (!this.ditheringLib$bl || !DitheringLibClient.isEnabled()) {
			original.call(instance, pipeline);
			return;
		}
		this.ditheringLib$bl = false;
		if (IrisAPI.isShaderPackInUse()) {
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

	@Override
	public void ditheringLib$setDithering(boolean bl) {
		this.ditheringLib$bl = bl;
	}

	@Override
	public boolean ditheringLib$isDithering() {
		return this.ditheringLib$bl;
	}
}

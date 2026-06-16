package net.lopymine.dl.mixin.iris;

import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.iris.IrisDitheringShaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IrisRenderingPipeline.class)
public class IrisRenderingPipelineMixin {

	@Inject(method = "destroy", at = @At("HEAD"))
	private void ditheringLib$clearDitheringShaders(CallbackInfo ci) {
		if (!DitheringLibClient.isEnabled()) {
			return;
		}
		IrisDitheringShaderManager.clear();
	}
}

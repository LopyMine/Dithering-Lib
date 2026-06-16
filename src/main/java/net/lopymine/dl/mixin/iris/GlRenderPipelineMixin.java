package net.lopymine.dl.mixin.iris;

import com.mojang.blaze3d.opengl.*;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.compat.LoadedMods;
import net.lopymine.dl.utils.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlRenderPipeline.class)
public class GlRenderPipelineMixin {

	@Shadow @Final private RenderPipeline info;

	@Inject(method = "program", at = @At("RETURN"), cancellable = true)
	private void ditheringLib$swapDithering(CallbackInfoReturnable<GlProgram> cir) {
		if (!DitheringLibClient.isEnabled()) {
			return;
		}
		DitheringMarker thing = (DitheringMarker) this.info;
		if (!thing.ditheringLib$isDithering()) {
			return;
		}
		if (!LoadedMods.IRIS_LOADED) {
			return;
		}
		GlProgram ditheringProgram = ((ProgramContainer) cir.getReturnValue()).ditheringLib$get();
		if (ditheringProgram != null) {
			cir.setReturnValue(ditheringProgram);
		}
	}
}

package net.lopymine.dl.mixin.pipeline;

import net.lopymine.dl.dithering.DitheringRenderPipelineManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 2042)
public class MinecraftMixin {

	@Inject(at = @At("HEAD"), method = "onGameLoadFinished(Lnet/minecraft/client/Minecraft$GameLoadCookie;)V")
	private void registerPipelines(CallbackInfo ci) {
		DitheringRenderPipelineManager.registerAndClear();
	}

}

package net.lopymine.dl.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.thing.RenderingMarker;
import net.lopymine.dl.utils.IrisDitheringMarker;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockModelRenderState.class, priority = 802)
public class BlockModelRenderStateMixin {

	@Inject(at = @At("HEAD"), method = "submitModel")
	private void enableDitheringForBlockModel(CallbackInfo ci, @Local(argsOnly = true) RenderType renderType) {
		if (!DitheringLibClient.isEnabled()) {
			return;
		}
		boolean bl = RenderingMarker.DITHERING_ENABLED.get().isEnabled();
		if (bl && renderType != null) {
			((IrisDitheringMarker) renderType).ditheringLib$setDithering(true);
		}
	}

}

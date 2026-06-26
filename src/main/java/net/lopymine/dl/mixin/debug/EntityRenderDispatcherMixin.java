package net.lopymine.dl.mixin.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.DitheringData;
import net.lopymine.dl.thing.RenderingMarker;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

//? if >=26.1 {
import net.minecraft.client.renderer.state.level.CameraRenderState;
//?} else {
/*import net.minecraft.client.renderer.state.CameraRenderState;
*///?}

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

	@Inject(at = @At("HEAD"), method = "submit")
	private void markEntity1(EntityRenderState entityRenderState, CameraRenderState cameraRenderState, double d, double e, double f, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
		if (DitheringLibClient.debug) {
			RenderingMarker.DITHERING_ENABLED.get().setEnabled(true);
			DitheringData data = new DitheringData();
			data.setPixelSize(1.0F);
			data.setFar(0.0F);
			data.setNear(0.0F);
			data.setFixedValue(0.4F);
			data.setMinValue(0.0F);
			data.push();
		}
	}

	@Inject(at = @At("TAIL"), method = "submit")
	private void markEntity2(EntityRenderState entityRenderState, CameraRenderState cameraRenderState, double d, double e, double f, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
		if (DitheringLibClient.debug) {
			RenderingMarker.DITHERING_ENABLED.get().setEnabled(false);
		}
	}

}

package net.lopymine.dl.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.Iterator;
import net.lopymine.dl.DitheringLib;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.*;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringDataBuffer;
import net.lopymine.dl.thing.RenderingMarker;
import net.lopymine.dl.utils.DitheringDataContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public abstract class FeatureRenderDispatcherMixin {

	@Unique
	private Boolean currentDispatcherIsVanilla = null;

	@Unique
	private boolean ditheringTranslucentRendering;
	@Unique
	private boolean ditheringSolidRendering;

	@Shadow public abstract void renderTranslucentFeatures();

	@Shadow public abstract void renderSolidFeatures();

	@Shadow @Final private BufferSource bufferSource;

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
			),
			method = {"renderSolidFeatures", "renderTranslucentFeatures"}
	)
	private Object pushData(Iterator<?> instance, Operation<Object> original) {
		if (!this.isDitheringRendering() || this.notUseDitheringDispatcherSource()) {
			return original.call(instance);
		}

		SubmitNodeCollection collection = (SubmitNodeCollection) original.call(instance);
		DitheringData data = ((DitheringDataContainer) collection).ditheringLib$getData();
		if (data == null) {
			DitheringLibClient.LOGGER.error("Activated dithering rendering but data hasn't been pushed!", new Throwable());
			return collection;
		}

		data.push();
		VanillaDitheringDataBuffer.update(data);

		return collection;
	}

	@Inject(at = @At("TAIL"), method = "renderSolidFeatures")
	private void renderSolidAgainButDithering(CallbackInfo ci) {
		if (!DitheringLibClient.isEnabled() || this.notUseDitheringDispatcherSource()) {
			return;
		}
		if (this.ditheringSolidRendering || this.ditheringTranslucentRendering) {
			return;
		}

		this.bufferSource.endBatch();
		RenderingMarker.API_DITHERING_RENDERING.get().setEnabled(true);
		this.ditheringSolidRendering = true;
		this.renderSolidFeatures();
		this.bufferSource.endBatch();
		this.ditheringSolidRendering = false;
		RenderingMarker.API_DITHERING_RENDERING.get().setEnabled(false);
	}

	@Inject(at = @At("TAIL"), method = "renderTranslucentFeatures")
	private void renderTranslucentAgainButDithering(CallbackInfo ci) {
		if (!DitheringLibClient.isEnabled() || this.notUseDitheringDispatcherSource()) {
			return;
		}
		if (this.ditheringSolidRendering || this.ditheringTranslucentRendering) {
			return;
		}

		this.bufferSource.endBatch();
		RenderingMarker.API_DITHERING_RENDERING.get().setEnabled(true);
		this.ditheringTranslucentRendering = true;
		this.renderTranslucentFeatures();
		this.bufferSource.endBatch();
		this.ditheringTranslucentRendering = false;
		RenderingMarker.API_DITHERING_RENDERING.get().setEnabled(false);
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/SubmitNodeStorage;getSubmitsPerOrder()Lit/unimi/dsi/fastutil/ints/Int2ObjectAVLTreeMap;"),
			method = {"renderSolidFeatures", "renderTranslucentFeatures"}
	)
	private Int2ObjectAVLTreeMap<SubmitNodeCollection> swapToDitheringModelSet(SubmitNodeStorage instance, Operation<Int2ObjectAVLTreeMap<SubmitNodeCollection>> original) {
		if (!this.isDitheringRendering() || this.notUseDitheringDispatcherSource()) {
			return original.call(instance);
		}
		return DitheringRenderManager.getInstance().getStorage().getSubmitsPerOrder();
	}

	@Unique
	private boolean notUseDitheringDispatcherSource() {
		this.checkIfCurrentVanillaDispatcher();
		return !this.currentDispatcherIsVanilla && !DitheringRenderManager.getInstance().isRedirectDispatcher();
	}

	@Unique
	private boolean isDitheringRendering() {
		return this.ditheringSolidRendering || this.ditheringTranslucentRendering;
	}

	@Unique
	private void checkIfCurrentVanillaDispatcher() {
		if (this.currentDispatcherIsVanilla != null) {
			return;
		}

		FeatureRenderDispatcher dispatcher = (FeatureRenderDispatcher) (Object) (this);
		this.currentDispatcherIsVanilla = dispatcher == Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
	}

}

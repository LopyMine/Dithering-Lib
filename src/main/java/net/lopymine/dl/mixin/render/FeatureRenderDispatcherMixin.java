package net.lopymine.dl.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.Iterator;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.*;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringDataBuffer;
import net.lopymine.dl.thing.RenderingMarker;
import net.lopymine.dl.utils.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public abstract class FeatureRenderDispatcherMixin {

	@Unique
	private boolean ditheringTranslucentRendering;
	@Unique
	private boolean ditheringSolidRendering;

	//? if >=26.1 {
	@Shadow
	public abstract void renderTranslucentFeatures();

	@Shadow
	public abstract void renderSolidFeatures();
	//?} else {
	/*@Shadow public abstract void renderAllFeatures();
	*///?}

	@Shadow @Final private BufferSource bufferSource;

	@Shadow @Final private SubmitNodeStorage submitNodeStorage;

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
			),
			//? if >=26.1 {
			method = {"renderSolidFeatures", "renderTranslucentFeatures"}
			//?} else {
			/*method = {"renderAllFeatures"}
			*///?}
	)
	private Object pushData(Iterator<?> instance, Operation<Object> original) {
		if (!this.isDitheringRendering()) {
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

	//? if >=26.1 {
	@Inject(at = @At("TAIL"), method = "renderSolidFeatures")
	private void renderSolidAgainButDithering(CallbackInfo ci) {
		if (!DitheringLibClient.isEnabled()) {
			return;
		}
		if (this.ditheringSolidRendering || this.ditheringTranslucentRendering) {
			return;
		}
		if (((DitheringMarker) this.submitNodeStorage).ditheringLib$getDitheringStorage().getSubmitsPerOrder().isEmpty()) {
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
		if (!DitheringLibClient.isEnabled()) {
			return;
		}
		if (this.ditheringSolidRendering || this.ditheringTranslucentRendering) {
			return;
		}
		if (((DitheringMarker) this.submitNodeStorage).ditheringLib$getDitheringStorage().getSubmitsPerOrder().isEmpty()) {
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
	//?} else {
	/*@Inject(method = "renderAllFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeStorage;clear()V"))
		private void renderSolidAgainButDithering(CallbackInfo ci) {
			if (!DitheringLibClient.isEnabled()) {
				return;
			}
			if (this.ditheringSolidRendering || this.ditheringTranslucentRendering) {
				return;
			}
			if (((DitheringMarker) this.submitNodeStorage).ditheringLib$getDitheringStorage().getSubmitsPerOrder().isEmpty()) {
				return;
			}

			this.bufferSource.endBatch();
			RenderingMarker.API_DITHERING_RENDERING.get().setEnabled(true);
			this.ditheringSolidRendering = true;
			this.ditheringTranslucentRendering = true;
			this.renderAllFeatures();
			this.bufferSource.endBatch();
			this.ditheringSolidRendering = false;
			this.ditheringTranslucentRendering = false;
			RenderingMarker.API_DITHERING_RENDERING.get().setEnabled(false);
		}
	*///?}

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/SubmitNodeStorage;getSubmitsPerOrder()Lit/unimi/dsi/fastutil/ints/Int2ObjectAVLTreeMap;"),
			//? if >=26.1 {
			method = {"renderSolidFeatures", "renderTranslucentFeatures"}
			//?} else {
			/*method = {"renderAllFeatures"}
			*///?}
	)
	private Int2ObjectAVLTreeMap<SubmitNodeCollection> swapToDitheringModelSet(SubmitNodeStorage instance, Operation<Int2ObjectAVLTreeMap<SubmitNodeCollection>> original) {
		if (!this.isDitheringRendering()) {
			return original.call(instance);
		}
		return ((DitheringMarker) instance).ditheringLib$getDitheringStorage().getSubmitsPerOrder();
	}

	@Unique
	private boolean isDitheringRendering() {
		return this.ditheringSolidRendering || this.ditheringTranslucentRendering;
	}

}

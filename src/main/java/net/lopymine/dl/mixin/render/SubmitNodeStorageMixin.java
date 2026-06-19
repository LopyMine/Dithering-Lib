package net.lopymine.dl.mixin.render;

import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.*;
import net.lopymine.dl.thing.RenderingMarker;
import net.lopymine.dl.utils.*;
import net.minecraft.client.renderer.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(SubmitNodeStorage.class)
public class SubmitNodeStorageMixin implements DitheringMarker {

	@Unique
	private boolean ditheringStorage = false;

	@Unique
	@Nullable
	private SubmitNodeStorage ditheringLib$storage = null;

	@Inject(at = @At("RETURN"), method = "order(I)Lnet/minecraft/client/renderer/SubmitNodeCollection;", cancellable = true)
	private void swapOrderToDithering(int order, CallbackInfoReturnable<SubmitNodeCollection> cir) {
		if (!DitheringLibClient.isEnabled() || this.ditheringStorage) {
			return;
		}
		if (!RenderingMarker.DITHERING_ENABLED.get().isEnabled()) {
			return;
		}

		SubmitNodeCollection submitNodeCollection = this.ditheringLib$getDitheringStorage().order(order);
		((DitheringDataContainer) submitNodeCollection).ditheringLib$setData(DitheringData.CURRENT_DITHERING_DATA.get());
		cir.setReturnValue(submitNodeCollection);
	}

	@Inject(at = @At("HEAD"), method = "endFrame")
	private void endFrameDitheringStorage(CallbackInfo ci) {
		if (this.ditheringStorage || this.ditheringLib$storage == null) {
			return;
		}
		this.ditheringLib$storage.endFrame();
	}

	@Inject(at = @At("HEAD"), method = "clear")
	private void clearDitheringStorage(CallbackInfo ci) {
		if (this.ditheringStorage || this.ditheringLib$storage == null) {
			return;
		}
		this.ditheringLib$storage.clear();
	}

	@Override
	public void ditheringLib$setDithering(boolean bl) {
		this.ditheringStorage = bl;
	}

	@Override
	public boolean ditheringLib$isDithering() {
		return this.ditheringStorage;
	}

	@Override
	public SubmitNodeStorage ditheringLib$getDitheringStorage() {
		if (this.ditheringStorage) {
			return (SubmitNodeStorage) (Object) this;
		}
		if (this.ditheringLib$storage == null) {
			this.ditheringLib$storage = DitheringRenderManager.createDitheringStorage();
		}
		return this.ditheringLib$storage;
	}
}

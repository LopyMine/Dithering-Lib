package net.lopymine.dl.mixin.render;

import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.*;
import net.lopymine.dl.thing.RenderingMarker;
import net.lopymine.dl.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(SubmitNodeStorage.class)
public class SubmitNodeStorageMixin implements DitheringMarker {

	@Unique
	private boolean ditheringStorage = false;

	@Unique
	private Boolean currentStorageIsVanilla = null;

	@Inject(at = @At("RETURN"), method = "order(I)Lnet/minecraft/client/renderer/SubmitNodeCollection;", cancellable = true)
	private void swapOrderToDithering(int order, CallbackInfoReturnable<SubmitNodeCollection> cir) {
		if (!DitheringLibClient.isEnabled() || this.notUseDitheringStorageSource()) {
			return;
		}

		boolean bl = RenderingMarker.DITHERING_ENABLED.get().isEnabled();
		if (bl) {
			SubmitNodeStorage storage = DitheringRenderManager.getInstance().getStorage();
			SubmitNodeCollection submitNodeCollection = storage.order(order);
			((DitheringDataContainer) submitNodeCollection).ditheringLib$setData(DitheringData.CURRENT_DITHERING_DATA.get());
			cir.setReturnValue(submitNodeCollection);
		}
	}

	@Inject(at = @At("HEAD"), method = "endFrame")
	private void endFrameDitheringStorage(CallbackInfo ci) {
		if (this.notUseDitheringStorageSource()) {
			return;
		}

		DitheringRenderManager.getInstance().getStorage().endFrame();
	}

	@Inject(at = @At("HEAD"), method = "clear")
	private void clearDitheringStorage(CallbackInfo ci) {
		if (this.notUseDitheringStorageSource()) {
			return;
		}

		DitheringRenderManager.getInstance().getStorage().clear();
	}

	@Override
	public void ditheringLib$setDithering(boolean bl) {
		this.ditheringStorage = bl;
	}

	@Override
	public boolean ditheringLib$isDithering() {
		return this.ditheringStorage;
	}

	@Unique
	private boolean notUseDitheringStorageSource() {
		this.checkIfCurrentVanillaStorage();
		return this.ditheringStorage || (!this.currentStorageIsVanilla && !DitheringRenderManager.getInstance().isRedirectStorage());
	}

	@Unique
	private void checkIfCurrentVanillaStorage() {
		if (this.currentStorageIsVanilla != null) {
			return;
		}
		SubmitNodeStorage submitNodeStorage = (SubmitNodeStorage) (Object) (this);
		this.currentStorageIsVanilla = submitNodeStorage == Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher().getSubmitNodeStorage();
	}
}

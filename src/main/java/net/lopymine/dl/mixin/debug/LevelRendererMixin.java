package net.lopymine.dl.mixin.debug;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.*;

@Debug(export = true)
@Mixin(value = {LevelRenderer.class, FeatureRenderDispatcher.class, BufferSource.class})
public class LevelRendererMixin {


}

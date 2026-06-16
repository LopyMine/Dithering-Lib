package net.lopymine.dl.mixin.shader;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.shaders.ShaderType;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.vanilla.*;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlDevice")
public class GlDeviceMixin {

	@WrapOperation(
			method = "compileShader",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/shaders/ShaderSource;get(Lnet/minecraft/resources/Identifier;Lcom/mojang/blaze3d/shaders/ShaderType;)Ljava/lang/String;"
			)
	)
	private String ditheringLib$injectDitheringSource(ShaderSource instance, Identifier id, ShaderType type, Operation<String> original) {
		Identifier base = VanillaDitheringShaderManager.getDitheringShaderId(id);
		if (!DitheringLibClient.isEnabled() || base == null) {
			return instance.get(id, type);
		}
		String baseSource = instance.get(base, type);
		return baseSource == null ? null : VanillaDitherShaderPatcher.patchFragmentShader(baseSource);
	}
}

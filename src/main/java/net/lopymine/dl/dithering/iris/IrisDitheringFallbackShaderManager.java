package net.lopymine.dl.dithering.iris;

import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.vertex.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import net.irisshaders.iris.gl.GLDebug;
import net.irisshaders.iris.gl.blending.*;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.state.*;
import net.irisshaders.iris.pipeline.*;
import net.irisshaders.iris.pipeline.fallback.ShaderSynthesizer;
import net.irisshaders.iris.pipeline.programs.*;
import net.irisshaders.iris.pipeline.transform.*;
import net.irisshaders.iris.uniforms.*;
import net.lopymine.dl.client.DitheringLibClient;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;

public class IrisDitheringFallbackShaderManager {

	public static ShaderSupplier wrapWithDithering(
			ShaderSupplier original, String name, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent,
			AlphaTest alpha, VertexFormat vertexFormat, BlendModeOverride blendModeOverride, IrisRenderingPipeline parent, FogMode fogMode,
			boolean entityLighting, boolean isGlint, boolean isText, boolean intensityTex, boolean isFullbright
	) {
		Supplier<GlProgram> originalSupplier = original.shader();

		Supplier<GlProgram> wrapped = () -> {
			GlProgram originalProgram = originalSupplier.get();
			try {
				GlProgram ditheringProgram = buildDithering(name, writingToBeforeTranslucent, writingToAfterTranslucent, alpha, vertexFormat, blendModeOverride, parent, fogMode, entityLighting, isGlint, isText, intensityTex, isFullbright);
				if (ditheringProgram != null) {
					IrisDitheringShaderManager.register(originalProgram, ditheringProgram);
				}
			} catch (IOException e) {
				throw new RuntimeException("Failed to build dithering copy for fallback shader \"" + name + "\"", e);
			}
			return originalProgram;
		};

		return new ShaderSupplier(original.key(), original.id(), wrapped);
	}

	@Nullable
	private static GlProgram buildDithering(
			String name, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent,
			AlphaTest alpha, VertexFormat vertexFormat, BlendModeOverride blendModeOverride, IrisRenderingPipeline parent, FogMode fogMode,
			boolean entityLighting, boolean isGlint, boolean isText, boolean intensityTex, boolean isFullbright
	) throws IOException {
		ShaderAttributeInputs inputs = new ShaderAttributeInputs(vertexFormat, isFullbright, false, isGlint, isText, false);
		boolean isLeash = vertexFormat == DefaultVertexFormat.POSITION_COLOR_LIGHTMAP;
		String vertex = ShaderSynthesizer.vsh(true, inputs, fogMode, entityLighting, isLeash);
		String fragment = ShaderSynthesizer.fsh(inputs, fogMode, alpha, intensityTex, isLeash);

		String patched = IrisFallbackShaderPatcher.patchFragmentShader(fragment);
		if (fragment.equals(patched)) {
			DitheringLibClient.LOGGER.error("Failed to patch Fallback Iris GlProgram: \"{}\"", name);
			return null;
		}

		String ditherName = name + "_dithering_lib";

		DitheringLibClient.LOGGER.info("Copied Fallback Iris GlProgram: \"{}\"", ditherName);

		ShaderPrinter.printProgram(ditherName).addSource(PatchShaderType.VERTEX, vertex).addSource(PatchShaderType.FRAGMENT, patched).print();
		PartialShader id = ShaderCreator.link(ditherName, vertex, null, null, null, patched, vertexFormat, true);

		GLDebug.nameObject(33506, id.program(), ditherName + "_fallback");
		return new FallbackShader(id.getFinally(), RenderPipelines.ENTITY_CUTOUT, ditherName, vertexFormat, writingToBeforeTranslucent, writingToAfterTranslucent, blendModeOverride, alpha.reference(), parent);
	}

}

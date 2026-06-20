package net.lopymine.dl.mixin.pipeline;

import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.pipeline.RenderPipeline.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.*;
import net.lopymine.dl.DitheringLib;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.DitheringRenderPipelineManager;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringTargets;
import net.lopymine.dl.dithering.vanilla.VanillaDitheringShaderManager;
import net.lopymine.dl.utils.BuilderCopyDithering;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(RenderPipeline.Builder.class)
public abstract class RenderPipelineBuilderMixin implements BuilderCopyDithering {

	@Shadow
	private Optional<Identifier> location;
	@Shadow
	private Optional<Identifier> fragmentShader;
	@Shadow
	private Optional<Identifier> vertexShader;
	@Shadow
	private Optional<ShaderDefines.Builder> definesBuilder;
	@Shadow
	private Optional<List<String>> samplers;
	@Shadow
	private Optional<List<UniformDescription>> uniforms;
	//? >=26.1 {
    /*@Shadow
	private Optional<DepthStencilState> depthStencilState;
	@Shadow
	private Optional<ColorTargetState> colorTargetState;
    *///?} else {
	@Shadow private Optional<DepthTestFunction> depthTestFunction;
	@Shadow private Optional<Boolean> writeDepth;
	@Shadow private float depthBiasScaleFactor;
	@Shadow private float depthBiasConstant;
	@Shadow private Optional<Boolean> writeColor;
	@Shadow private Optional<Boolean> writeAlpha;
	@Shadow private Optional<LogicOp> colorLogic;
	@Shadow private Optional<BlendFunction> blendFunction;
	//?}
	@Shadow
	private Optional<PolygonMode> polygonMode;
	@Shadow
	private Optional<Boolean> cull;
	@Shadow
	private Optional<VertexFormat> vertexFormat;
	@Shadow
	private Optional<Mode> vertexFormatMode;

	@Shadow public abstract Builder withUniform(String name, UniformType type);

	@Inject(at = @At("RETURN"), method = "build")
	private void ditheringLib$createDitheringCopy(CallbackInfoReturnable<RenderPipeline> cir) {
		if (cir.getReturnValue().getLocation().getPath().endsWith("dithering")) {
			return;
		}
		DitheringRenderPipelineManager.savePipelineBuilder(cir.getReturnValue(), (Builder) (Object) this);
	}

	@Override
	public void ditheringLib$copyDithering(Builder builder) {
		if (!DitheringLibClient.isEnabled()) {
			return;
		}

		RenderPipelineBuilderAccessor accessor = (RenderPipelineBuilderAccessor) builder;

		this.location = accessor.getLocation().map((id) -> DitheringLib.id("%s/%s/dithering".formatted(id.getNamespace(), id.getPath())));
		this.fragmentShader = Optional.of(VanillaDitheringShaderManager.createDitheringShaderId(accessor.getFragmentShader().orElseThrow()));
		this.vertexShader = accessor.getVertexShader();
		this.definesBuilder = accessor.getDefinesBuilder();
		this.samplers = accessor.getSamplers().map(ArrayList::new);
		this.uniforms = accessor.getUniforms().map(ArrayList::new);
		this.withUniform("DitheringLibData", UniformType.UNIFORM_BUFFER);

		//? >=26.1 {
        /*this.depthStencilState = accessor.getDepthStencilState();
		this.colorTargetState = accessor.getColorTargetState();
		*///?} else {
		this.depthTestFunction = accessor.getDepthTestFunction();
		this.writeDepth = accessor.getWriteDepth();
		this.depthBiasScaleFactor = accessor.getDepthBiasScaleFactor();
		this.depthBiasConstant = accessor.getDepthBiasConstant();
		this.writeColor = accessor.getWriteColor();
		this.writeAlpha = accessor.getWriteAlpha();
		this.colorLogic = accessor.getColorLogic();
		this.blendFunction = accessor.getBlendFunction();
		//?}

		this.polygonMode = accessor.getPolygonMode();
		this.cull = accessor.getCull();
		this.vertexFormat = accessor.getVertexFormat();
		this.vertexFormatMode = accessor.getVertexFormatMode();

		DitheringLibClient.LOGGER.info("Copied RenderPipeline: \"{}\"", this.location);
	}
}
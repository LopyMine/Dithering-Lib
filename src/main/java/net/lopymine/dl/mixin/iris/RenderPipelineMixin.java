package net.lopymine.dl.mixin.iris;

import com.mojang.blaze3d.opengl.GlProgram;
import net.lopymine.dl.utils.ProgramContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(GlProgram.class)
public class RenderPipelineMixin implements ProgramContainer {

	@Unique
	private GlProgram ditheringLib$program;

	@Override
	public @Nullable GlProgram ditheringLib$get() {
		return this.ditheringLib$program;
	}

	@Override
	public void ditheringLib$set(@Nullable GlProgram program) {
		this.ditheringLib$program = program;
	}
}

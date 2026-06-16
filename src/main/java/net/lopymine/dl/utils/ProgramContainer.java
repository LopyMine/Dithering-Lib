package net.lopymine.dl.utils;

import com.mojang.blaze3d.opengl.GlProgram;
import org.jetbrains.annotations.Nullable;

public interface ProgramContainer {

	@Nullable GlProgram ditheringLib$get();

	void ditheringLib$set(@Nullable GlProgram program);

}

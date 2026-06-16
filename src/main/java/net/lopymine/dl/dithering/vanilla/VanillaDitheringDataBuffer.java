package net.lopymine.dl.dithering.vanilla;

import com.mojang.blaze3d.buffers.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.lopymine.dl.dithering.DitheringData;
import org.lwjgl.system.MemoryStack;

public class VanillaDitheringDataBuffer {

	private static final int SIZE = new Std140SizeCalculator()
			.putFloat()
			.putFloat()
			.putFloat()
			.putFloat()
			.putFloat()
			.get();

	private static final GpuBuffer BUFFER = RenderSystem.getDevice().createBuffer(() -> "VanillaDitheringDataBuffer UBO", 136, SIZE);

	public static void update() {
		DitheringData data = DitheringData.getInstance();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, SIZE)
					.putFloat(data.getFar())
					.putFloat(data.getNear())
					.putFloat((float) Math.clamp(data.getMinValue(), 0.0, 1.0F))
					.putFloat((float) Math.clamp(data.getFixedValue(), 0.0, 1.0F))
					.putFloat(data.getPixelSize())
					.get();
			RenderSystem.getDevice().createCommandEncoder().writeToBuffer(BUFFER.slice(), byteBuffer);
		}
	}

	public static GpuBuffer getBuffer() {
		return BUFFER;
	}
}

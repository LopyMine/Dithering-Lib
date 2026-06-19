package net.lopymine.dl.dithering;

import lombok.*;
import net.lopymine.dl.utils.DitheringMarker;
import net.minecraft.client.renderer.*;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class DitheringRenderManager {

	private final SubmitNodeStorage storage;
	private boolean redirectStorage;
	private boolean redirectDispatcher;

	private static final DitheringRenderManager INSTANCE = new DitheringRenderManager();

	public static DitheringRenderManager getInstance() {
		return INSTANCE;
	}

	private DitheringRenderManager() {
		this.storage = new SubmitNodeStorage();
		((DitheringMarker)this.storage).ditheringLib$setDithering(true);
	}
}

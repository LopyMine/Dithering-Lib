package net.lopymine.dl.api;

import java.util.*;
import lombok.Getter;
import net.lopymine.dl.client.DitheringLibClient;
import net.lopymine.dl.dithering.DitheringData;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public class DitheringLibAPI {

	@Getter
	private final Set<Identifier> vanillaTargets = new HashSet<>();
	@Getter
	private final Set<String> irisTargets = new HashSet<>();

	private static final DitheringLibAPI INSTANCE = new DitheringLibAPI();

	public static DitheringLibAPI getInstance() {
		DitheringLibClient.setEnabled(true);
		return INSTANCE;
	}

	public DitheringData getData() {
		return DitheringData.getInstance();
	}

}

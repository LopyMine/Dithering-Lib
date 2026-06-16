package net.lopymine.dl.dithering.vanilla;

import net.lopymine.dl.api.DitheringLibAPI;
import net.minecraft.resources.Identifier;

public class VanillaDitheringTargets {

	public static boolean isTarget(Identifier fragmentShader) {
		return fragmentShader != null && DitheringLibAPI.getInstance().getVanillaTargets().contains(fragmentShader);
	}
}

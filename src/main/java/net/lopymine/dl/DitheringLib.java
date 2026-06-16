package net.lopymine.dl;

import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class DitheringLib {

	public static final String MOD_NAME = /*$ mod_name*/ "DitheringLib";
	public static final String MOD_ID = /*$ mod_id*/ "dithering_lib";
	public static final MossyLogger LOGGER = new MossyLogger(MOD_NAME);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void onInitialize() {
		LOGGER.info("{} Initialized", MOD_NAME);
	}
}
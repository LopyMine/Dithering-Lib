package net.lopymine.dl.client;

import lombok.*;
import net.lopymine.mossylib.logger.MossyLogger;

public class DitheringLibClient {

	public static final MossyLogger LOGGER = net.lopymine.dl.DitheringLib.LOGGER.extend("Client");

	@Getter
	@Setter
	public static boolean enabled;

	public static void onInitializeClient() {
		LOGGER.info("{} Client Initialized", net.lopymine.dl.DitheringLib.MOD_NAME);
	}
}

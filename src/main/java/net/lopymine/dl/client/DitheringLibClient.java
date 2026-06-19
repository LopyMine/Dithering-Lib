package net.lopymine.dl.client;

import java.util.Set;
import lombok.*;
import net.lopymine.dl.DitheringLib;
import net.lopymine.dl.api.DitheringLibAPI;
import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.resources.Identifier;

public class DitheringLibClient {

	public static final MossyLogger LOGGER = net.lopymine.dl.DitheringLib.LOGGER.extend("Client");

	@Getter
	@Setter
	public static boolean enabled;

	@Getter
	public static boolean debug;

	private static final Set<Identifier> VANILLA_TARGETS = Set.of(
			Identifier.fromNamespaceAndPath("minecraft", "core/entity"),
			Identifier.fromNamespaceAndPath("minecraft", "core/item")
	);
	private static final Set<Identifier> VANILLA_TARGETS_2 = Set.of(
			Identifier.fromNamespaceAndPath("minecraft", "core/entity"),
			Identifier.fromNamespaceAndPath("minecraft", "core/item")
	);
	private static final Set<Identifier> VANILLA_TARGETS_3 = Set.of(
			Identifier.fromNamespaceAndPath("minecraft", "core/particle")
	);

	private static final Set<String> IRIS_TARGETS = Set.of("entities", "entitiestrans");
	private static final Set<String> IRIS_TARGETS_2 = Set.of("hand", "hand_water");
	private static final Set<String> IRIS_TARGETS_3 = Set.of("particles", "particlestrans");

	public static void onInitializeClient() {
		LOGGER.info("{} Client Initialized", DitheringLib.MOD_NAME);

		debug = Boolean.getBoolean("dithering_lib.debug");
		if (debug) {
			DitheringLibAPI api = DitheringLibAPI.getInstance();
			api.getIrisTargets().addAll(IRIS_TARGETS);
			api.getIrisTargets().addAll(IRIS_TARGETS_2);
			api.getIrisTargets().addAll(IRIS_TARGETS_3);

			api.getVanillaTargets().addAll(VANILLA_TARGETS);
			api.getVanillaTargets().addAll(VANILLA_TARGETS_2);
			api.getVanillaTargets().addAll(VANILLA_TARGETS_3);
		}
	}
}

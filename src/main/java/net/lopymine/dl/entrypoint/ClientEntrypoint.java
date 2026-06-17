package net.lopymine.dl.entrypoint;

//? if fabric {

import net.fabricmc.api.ClientModInitializer;
import net.lopymine.dl.client.DitheringLibClient;

public class ClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		DitheringLibClient.onInitializeClient();
	}
}

//?} elif neoforge {
/*import net.lopymine.dl.DitheringLib;
import net.lopymine.dl.client.DitheringLibClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = DitheringLib.MOD_ID, dist = Dist.CLIENT)
public class ClientEntrypoint {

	public ClientEntrypoint() {
		DitheringLibClient.onInitializeClient();
	}

}

*///?}

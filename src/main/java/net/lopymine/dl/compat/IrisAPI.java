package net.lopymine.dl.compat;

import net.irisshaders.iris.api.v0.IrisApi;

public class IrisAPI {

	public static boolean isShaderPackInUse() {
		if (!LoadedMods.IRIS_LOADED) {
			return false;
		}
		return IrisApi.getInstance().isShaderPackInUse();
	}

}

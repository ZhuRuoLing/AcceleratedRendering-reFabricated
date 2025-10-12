package com.github.argon4w.acceleratedrendering.compat;

import java.util.List;

public class BoosterRequiredMixinPlugin extends AbstractCompatMixinPlugin {

	@Override
	protected List<String> getModIDs() {
		return List.of("mixinbooster", "connectormod");
	}
}

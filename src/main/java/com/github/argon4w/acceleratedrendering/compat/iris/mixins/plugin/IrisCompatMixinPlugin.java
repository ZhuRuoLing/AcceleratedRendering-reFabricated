package com.github.argon4w.acceleratedrendering.compat.iris.mixins.plugin;

import com.github.argon4w.acceleratedrendering.compat.AbstractCompatMixinPlugin;

import java.util.List;

public class IrisCompatMixinPlugin extends AbstractCompatMixinPlugin {

	@Override
	protected List<String> getModIDs() {
		return List.of("iris");
	}
}

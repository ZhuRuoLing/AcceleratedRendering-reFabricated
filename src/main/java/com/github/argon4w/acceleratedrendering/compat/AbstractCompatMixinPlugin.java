package com.github.argon4w.acceleratedrendering.compat;

import com.github.argon4w.acceleratedrendering.FabricUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public abstract class AbstractCompatMixinPlugin implements IMixinConfigPlugin {

	private final boolean shouldApply;

	public AbstractCompatMixinPlugin() {
		var shouldApply	= false;

		for (var id : getModIDs()) {
			if (FabricUtils.modExists(id)) {
				shouldApply = true;
			}
		}

		this.shouldApply = shouldApply;
	}

	protected abstract List<String> getModIDs();

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return shouldApply;
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public void preApply(
			String		targetClassName,
			ClassNode	targetClass,
			String		mixinClassName,
			IMixinInfo	mixinInfo
	) {

	}

	@Override
	public void postApply(
			String		targetClassName,
			ClassNode	targetClass,
			String		mixinClassName,
			IMixinInfo	mixinInfo
	) {

	}
}

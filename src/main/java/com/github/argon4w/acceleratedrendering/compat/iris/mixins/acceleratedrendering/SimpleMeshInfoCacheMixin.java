package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.interfaces.IIrisMeshInfo;
import com.github.argon4w.acceleratedrendering.compat.iris.interfaces.IIrisMeshInfoCache;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.SimpleMeshInfo;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.SimpleMeshInfoCache;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleCachedArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleMeshInfoCache.class)
public class SimpleMeshInfoCacheMixin implements IIrisMeshInfoCache {

	@Shadow(remap = false) @Final private SimpleCachedArray<SimpleMeshInfo> meshInfos;

	@Override
	public short getRenderedEntity(int i) {
		return ((IIrisMeshInfo) meshInfos.at(i)).getRenderedEntity();
	}

	@Override
	public short getRenderedBlockEntity(int i) {
		return ((IIrisMeshInfo) meshInfos.at(i)).getRenderedBlockEntity();
	}

	@Override
	public short getRenderedItem(int i) {
		return ((IIrisMeshInfo) meshInfos.at(i)).getRenderedItem();
	}
}

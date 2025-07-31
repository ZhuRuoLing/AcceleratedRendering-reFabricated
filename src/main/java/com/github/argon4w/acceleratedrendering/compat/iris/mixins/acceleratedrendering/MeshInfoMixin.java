package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.interfaces.IIrisMeshInfo;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.MeshInfo;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MeshInfo.class, remap = false)
public class MeshInfoMixin implements IIrisMeshInfo {

	@Unique private short renderedEntity;
	@Unique private short renderedBlockEntity;
	@Unique private short renderedItem;

	@Inject(method = "setupMeshInfo", at = @At("TAIL"))
	public void setIrisData(
			int				color,
			int				light,
			int				overlay,
			int				sharing,
			int				shouldCull,
			CallbackInfo	ci
	) {
		renderedEntity		= (short) CapturedRenderingState.INSTANCE.getCurrentRenderedEntity		();
		renderedBlockEntity	= (short) CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity	();
		renderedItem		= (short) CapturedRenderingState.INSTANCE.getCurrentRenderedItem		();
	}

	@Unique
	@Override
	public short getRenderedEntity() {
		return renderedEntity;
	}

	@Unique
	@Override
	public short getRenderedBlockEntity() {
		return renderedBlockEntity;
	}

	@Unique
	@Override
	public short getRenderedItem() {
		return renderedItem;
	}
}

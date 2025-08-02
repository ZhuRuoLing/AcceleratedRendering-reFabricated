package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.buffers.AcceleratedBufferSources;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CoreBuffers.class, remap = false)
public class CoreBuffersMixin {

	@ModifyReturnValue(
			method	= "getCoreBufferSources",
			at		= @At("RETURN")
	)
	private static AcceleratedBufferSources getShadowBufferSources(AcceleratedBufferSources original) {
		return ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? IrisCompatBuffers.SHADOW : original;
	}
}

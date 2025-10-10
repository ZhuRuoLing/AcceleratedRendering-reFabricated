package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.buffers.AcceleratedBufferSources;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.irisshaders.iris.pathways.HandRenderer;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CoreBuffers.class)
public class CoreBuffersMixin {

	@ModifyReturnValue(
			method	= "getCoreBufferSources",
			at		= @At("RETURN"),
			remap	= false
	)
	private static AcceleratedBufferSources getShadowBufferSources(AcceleratedBufferSources original) {
		if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
			return IrisCompatBuffers.SHADOW;
		}

		if (HandRenderer.INSTANCE.isActive()) {
			return IrisCompatBuffers.HAND;
		}

		return original;
	}
}

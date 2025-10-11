package com.github.argon4w.acceleratedrendering.compat.sodium.mixins;

import com.github.argon4w.acceleratedrendering.compat.sodium.SodiumCompatFeature;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VertexConsumerUtils.class)
public class VertexConsumerUtilsMixin {

	@Inject(
			method		= "convertOrLog",
			at			= @At("HEAD"),
			cancellable	= true,
			remap		= false
	)
	private static void disableOptimizedPath(VertexConsumer consumer, CallbackInfoReturnable<VertexBufferWriter> cir) {
		if (			SodiumCompatFeature	.isEnabled					()
				&&		SodiumCompatFeature	.shouldDisableOptimizedPath	()
				&& (	CoreFeature			.isRenderingLevel			()
				||		CoreFeature			.isRenderingHand			()
				||		CoreFeature			.isRenderingGui				())
		) {
			cir.setReturnValue(null);
		}
	}
}

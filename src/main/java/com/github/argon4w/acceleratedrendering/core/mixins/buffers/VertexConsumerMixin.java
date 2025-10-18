package com.github.argon4w.acceleratedrendering.core.mixins.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends IAcceleratedVertexConsumer {

	@Unique
	@Override
	default boolean isAccelerated() {
		return false;
	}
}

package com.github.argon4w.acceleratedrendering.core.mixins.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ExtensionMethod(VertexConsumerExtension		.class)
@Mixin			(MultiBufferSource.BufferSource	.class)
public class BufferSourceMixin {

	@ModifyReturnValue(
			method	= "getBuffer",
			at		= @At("RETURN")
	)
	public VertexConsumer initAcceleration(VertexConsumer original, RenderType renderType) {
		return original
				.getHolder			()
				.initAcceleration	(renderType);
	}
}

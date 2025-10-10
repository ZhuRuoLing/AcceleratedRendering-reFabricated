package com.github.argon4w.acceleratedrendering.core.mixins;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public class RenderTypeMixin {

	@Inject(
			method		= "end",
			at			= @At(
					value	= "INVOKE",
					target	= "Lcom/mojang/blaze3d/vertex/BufferBuilder;end()Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;",
					shift	= At.Shift.BEFORE
			),
			cancellable	= true
	)
	public void fastEnd(
			BufferBuilder	bufferBuilder,
			VertexSorting	quadSorting,
			CallbackInfo	ci
	) {
		if (bufferBuilder.vertices == 0) {
			bufferBuilder	.reset	();
			ci				.cancel	();
		}
	}
}

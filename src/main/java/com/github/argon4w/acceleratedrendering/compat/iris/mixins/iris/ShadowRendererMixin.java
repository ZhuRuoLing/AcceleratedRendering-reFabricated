package com.github.argon4w.acceleratedrendering.compat.iris.mixins.iris;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisCompatBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreStates;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShadowRenderer.class)
public class ShadowRendererMixin {

	@Inject(
			method	= "renderShadows",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"
			),
			remap	= false
	)
	public void endAllBatches(
			LevelRendererAccessor	levelRenderer,
			Camera					playerCamera,
			CallbackInfo			ci
	) {
		CoreStates								.record			();
		IrisCompatBuffers.BLOCK_SHADOW			.prepareBuffers	();
		IrisCompatBuffers.ENTITY_SHADOW			.prepareBuffers	();
		IrisCompatBuffers.GLYPH_SHADOW			.prepareBuffers	();
		IrisCompatBuffers.POS_TEX_SHADOW		.prepareBuffers	();
		IrisCompatBuffers.POS_TEX_COLOR_SHADOW	.prepareBuffers	();
		CoreStates								.restore		();

		IrisCompatBuffers.BLOCK_SHADOW			.drawBuffers	(LayerDrawType.ALL);
		IrisCompatBuffers.ENTITY_SHADOW			.drawBuffers	(LayerDrawType.ALL);
		IrisCompatBuffers.GLYPH_SHADOW			.drawBuffers	(LayerDrawType.ALL);
		IrisCompatBuffers.POS_TEX_SHADOW		.drawBuffers	(LayerDrawType.ALL);
		IrisCompatBuffers.POS_TEX_COLOR_SHADOW	.drawBuffers	(LayerDrawType.ALL);

		IrisCompatBuffers.BLOCK_SHADOW			.clearBuffers	();
		IrisCompatBuffers.ENTITY_SHADOW			.clearBuffers	();
		IrisCompatBuffers.GLYPH_SHADOW			.clearBuffers	();
		IrisCompatBuffers.POS_TEX_SHADOW		.clearBuffers	();
		IrisCompatBuffers.POS_TEX_COLOR_SHADOW	.clearBuffers	();
	}
}

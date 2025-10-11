package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.CoreStates;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
			method = "renderItemInHand",
			at = @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/player/LocalPlayer;I)V",
					shift	= At.Shift.BEFORE
			)
	)
	public void startRenderItemInHandsFast(
			Camera			camera,
			float			partialTick,
			Matrix4f		projectionMatrix,
			CallbackInfo	ci
	) {
		CoreFeature.setRenderingHand();
	}

	@Inject(
			method = "renderItemInHand",
			at = @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/player/LocalPlayer;I)V",
					shift	= At.Shift.AFTER
			)
	)
	public void stopRenderItemInHandsFast(
			Camera			camera,
			float			partialTick,
			Matrix4f		projectionMatrix,
			CallbackInfo	ci
	) {
		CoreFeature						.resetRenderingHand	();

		CoreStates						.record				();
		CoreBuffers.ENTITY				.prepareBuffers		();
		CoreBuffers.BLOCK				.prepareBuffers		();
		CoreBuffers.POS					.prepareBuffers		();
		CoreBuffers.POS_TEX				.prepareBuffers		();
		CoreBuffers.POS_TEX_COLOR		.prepareBuffers		();
		CoreBuffers.POS_COLOR_TEX_LIGHT	.prepareBuffers		();
		CoreStates						.restore			();

		CoreBuffers.ENTITY				.drawBuffers		(LayerDrawType.ALL);
		CoreBuffers.BLOCK				.drawBuffers		(LayerDrawType.ALL);
		CoreBuffers.POS					.drawBuffers		(LayerDrawType.ALL);
		CoreBuffers.POS_TEX				.drawBuffers		(LayerDrawType.ALL);
		CoreBuffers.POS_TEX_COLOR		.drawBuffers		(LayerDrawType.ALL);
		CoreBuffers.POS_COLOR_TEX_LIGHT	.drawBuffers		(LayerDrawType.ALL);

		CoreBuffers.ENTITY				.clearBuffers		();
		CoreBuffers.BLOCK				.clearBuffers		();
		CoreBuffers.POS					.clearBuffers		();
		CoreBuffers.POS_TEX				.clearBuffers		();
		CoreBuffers.POS_TEX_COLOR		.clearBuffers		();
		CoreBuffers.POS_COLOR_TEX_LIGHT	.clearBuffers		();
	}
}

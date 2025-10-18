package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.CoreStates;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

	@Inject(method = "lambda$renderEntityInInventory$1", at = @At("HEAD"))
	private static void startRenderingGui(
			EntityRenderDispatcher	entityrenderdispatcher,
			LivingEntity			entity,
			GuiGraphics				guiGraphics,
			CallbackInfo			ci
	) {
		CoreFeature.setRenderingGui();
	}

	@Inject(method = "lambda$renderEntityInInventory$1", at = @At("TAIL"))
	private static void stopRenderingGui(
			EntityRenderDispatcher	entityrenderdispatcher,
			LivingEntity			entity,
			GuiGraphics				guiGraphics,
			CallbackInfo			ci
	) {
		CoreFeature.resetRenderingGui();

		if (!CoreFeature.isGuiBatching()) {
			CoreStates						.recordBuffers	();
			CoreBuffers.ENTITY				.prepareBuffers	();
			CoreBuffers.BLOCK				.prepareBuffers	();
			CoreBuffers.POS					.prepareBuffers	();
			CoreBuffers.POS_TEX				.prepareBuffers	();
			CoreBuffers.POS_TEX_COLOR		.prepareBuffers	();
			CoreBuffers.POS_COLOR_TEX_LIGHT	.prepareBuffers	();
			CoreStates						.restoreBuffers	();

			CoreBuffers.ENTITY				.drawBuffers	(LayerDrawType.ALL);
			CoreBuffers.BLOCK				.drawBuffers	(LayerDrawType.ALL);
			CoreBuffers.POS					.drawBuffers	(LayerDrawType.ALL);
			CoreBuffers.POS_TEX				.drawBuffers	(LayerDrawType.ALL);
			CoreBuffers.POS_TEX_COLOR		.drawBuffers	(LayerDrawType.ALL);
			CoreBuffers.POS_COLOR_TEX_LIGHT	.drawBuffers	(LayerDrawType.ALL);

			CoreBuffers.ENTITY				.clearBuffers	();
			CoreBuffers.BLOCK				.clearBuffers	();
			CoreBuffers.POS					.clearBuffers	();
			CoreBuffers.POS_TEX				.clearBuffers	();
			CoreBuffers.POS_TEX_COLOR		.clearBuffers	();
			CoreBuffers.POS_COLOR_TEX_LIGHT	.clearBuffers	();
		}
	}
}

package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
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

	@Inject(method = "method_29977", at = @At("HEAD"))
	private static void startRenderingGui(
			EntityRenderDispatcher	entityrenderdispatcher,
			LivingEntity			entity,
			GuiGraphics				guiGraphics,
			CallbackInfo			ci
	) {
		CoreFeature.setRenderingGui();
	}

	@Inject(method = "method_29977", at = @At("TAIL"))
	private static void stopRenderingGui(
			EntityRenderDispatcher	entityrenderdispatcher,
			LivingEntity			entity,
			GuiGraphics				guiGraphics,
			CallbackInfo			ci
	) {
		CoreFeature.resetRenderingGui();

		if (!CoreFeature.isGuiBatching()) {
			CoreBuffers.ENTITY				.drawBuffers();
			CoreBuffers.BLOCK				.drawBuffers();
			CoreBuffers.POS					.drawBuffers();
			CoreBuffers.POS_TEX				.drawBuffers();
			CoreBuffers.POS_TEX_COLOR		.drawBuffers();
			CoreBuffers.POS_COLOR_TEX_LIGHT	.drawBuffers();
		}
	}
}

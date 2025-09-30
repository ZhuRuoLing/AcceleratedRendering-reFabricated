package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ItemDecoratorHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemDecoratorHandler.class)
public class ItemDecorationHandlerMixin {

	@Inject(
			method	= "render",
			at		= @At(
					value	= "INVOKE",
					target	= "Lcom/mojang/blaze3d/systems/RenderSystem;backupGlState(Lnet/neoforged/neoforge/client/GlStateBackup;)V",
					shift	= At.Shift.AFTER
			)
	)
	public void startRenderingCustomDecoration(
			GuiGraphics		guiGraphics,
			Font			font,
			ItemStack		stack,
			int				xOffset,
			int				yOffset,
			CallbackInfo	ci
	) {
		if (CoreFeature.isGuiBatching()) {
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.bindWrite(false);
		}
	}

	@Inject(
			method	= "render",
			at		= @At(
					value	= "INVOKE",
					target	= "Lcom/mojang/blaze3d/systems/RenderSystem;restoreGlState(Lnet/neoforged/neoforge/client/GlStateBackup;)V",
					shift	= At.Shift.BEFORE
			)
	)
	public void stopRenderingCustomDecorationPart(
			GuiGraphics		guiGraphics,
			Font			font,
			ItemStack		stack,
			int				xOffset,
			int				yOffset,
			CallbackInfo	ci
	) {
		if (CoreFeature.isGuiBatching()) {
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.bindWrite(false);
		}
	}
}

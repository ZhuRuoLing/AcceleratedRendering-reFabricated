package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.features.items.gui.GuiBatchingController;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ItemDecoratorHandler;
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
					target	= "Lnet/minecraftforge/client/ItemDecoratorHandler;resetRenderState()V",
					shift	= At.Shift.AFTER,
					ordinal	= 0
			),
			remap	= false
	)
	public void startRenderingCustomDecoration(
			GuiGraphics		guiGraphics,
			Font			font,
			ItemStack		stack,
			int				xOffset,
			int				yOffset,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.useOverlayTarget(guiGraphics);
	}

	@Inject(
			method	= "render",
			at		= @At("TAIL"),
			remap	= false
	)
	public void stopRenderingCustomDecorationPart(
			GuiGraphics		guiGraphics,
			Font			font,
			ItemStack		stack,
			int				xOffset,
			int				yOffset,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.resetOverlayTarget();
	}
}

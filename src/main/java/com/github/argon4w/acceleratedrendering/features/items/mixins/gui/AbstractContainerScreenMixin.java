package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.features.items.gui.GuiBatchingController;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

	@Inject(
			method	= "render",
			at		= @At("HEAD")
	)
	public void startBatching(
			GuiGraphics		guiGraphics,
			int				mouseX,
			int				mouseY,
			float			partialTick,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.startBatching();
	}

	@Inject(
			method	= "render",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V",
					shift	= At.Shift.BEFORE
			)
	)
	public void flushBatching(
			GuiGraphics		guiGraphics,
			int				mouseX,
			int				mouseY,
			float			partialTick,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.flushBatching(guiGraphics);
	}

	@Inject(
			method	= "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;IIII)V",
			at		= @At("HEAD"),
			remap	= false
	)
	private static void startRenderHighlight(
			GuiGraphics		guiGraphics,
			int				mouseX,
			int				mouseY,
			int				blitOffset,
			int				color,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.useOverlayTarget();
	}

	@Inject(
			method	= "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;IIII)V",
			at		= @At("TAIL"),
			remap	= false
	)
	private static void stopRenderHighlight(
			GuiGraphics		guiGraphics,
			int				mouseX,
			int				mouseY,
			int				blitOffset,
			int				color,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.resetOverlayTarget();
	}
}

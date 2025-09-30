package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.GuiBatchingController;
import net.minecraft.client.Minecraft;
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
		GuiBatchingController.startBatching();
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
		GuiBatchingController.flushBatching(guiGraphics);
	}

	@Inject(
			method	= "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V",
			at		= @At("HEAD")
	)
	public void startRenderHighlight(
			GuiGraphics		guiGraphics,
			Slot			slot,
			int				mouseX,
			int				mouseY,
			float			partialTick,
			CallbackInfo	ci
	) {
		if (CoreFeature.isGuiBatching()) {
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.bindWrite(false);
		}
	}

	@Inject(
			method	= "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V",
			at		= @At("TAIL")
	)
	public void stopRenderHighlight(
			GuiGraphics		guiGraphics,
			Slot			slot,
			int				mouseX,
			int				mouseY,
			float			partialTick,
			CallbackInfo	ci
	) {
		if (CoreFeature.isGuiBatching()) {
			Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
		}
	}
}

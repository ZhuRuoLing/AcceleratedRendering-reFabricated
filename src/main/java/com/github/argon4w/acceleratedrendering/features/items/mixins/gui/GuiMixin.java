package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.features.items.GuiBatchingController;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

	@Inject(
			method	= "renderHotbar",
			at		= @At("HEAD")
	)
	public void startBatching(
			float			partialTicks,
			GuiGraphics		guiGraphics,
			CallbackInfo	ci
	) {
		GuiBatchingController.startBatching();
	}

	@Inject(
			method	= "renderHotbar",
			at		= @At("TAIL")
	)
	public void flushBatching(
			float			partialTicks,
			GuiGraphics		guiGraphics,
			CallbackInfo	ci
	) {
		GuiBatchingController.flushBatching(guiGraphics);
	}
}

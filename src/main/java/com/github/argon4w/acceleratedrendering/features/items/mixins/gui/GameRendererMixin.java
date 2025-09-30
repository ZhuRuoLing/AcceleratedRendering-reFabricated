package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(
			method	= "resize",
			at		= @At("TAIL")
	)
	public void onResize(
			int				width,
			int				height,
			CallbackInfo	ci
	) {
		AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.resize(
				width,
				height,
				Minecraft.ON_OSX
		);
	}
}

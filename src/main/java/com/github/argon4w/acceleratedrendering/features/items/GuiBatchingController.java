package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class GuiBatchingController {

	private static final boolean		ON_OSX			= Minecraft.ON_OSX;
	private static final Minecraft		MINECRAFT		= Minecraft.getInstance			();
	private static final RenderTarget	MAIN_TARGET		= MINECRAFT.getMainRenderTarget	();
	private static final Window			WINDOW			= MINECRAFT.getWindow			();

	public static void startBatching() {
		if (		AcceleratedItemRenderingFeature.isEnabled				()
				&&	AcceleratedItemRenderingFeature.shouldAccelerateInGui	()
				&&	AcceleratedItemRenderingFeature.shouldUseGuiItemBatching()
		) {
			CoreFeature.setGuiBatching();
		}
	}

	public static void flushBatching(GuiGraphics graphics) {
		if (CoreFeature.isGuiBatching()) {
			CoreFeature							.resetGuiBatching	();
			((IAcceleratedGuiGraphics) graphics).flushItemBatching	();

			RenderSystem.enableBlend		();
			RenderSystem.blendFuncSeparate	(
					GlStateManager.SourceFactor	.SRC_ALPHA,
					GlStateManager.DestFactor	.ONE_MINUS_SRC_ALPHA,
					GlStateManager.SourceFactor	.ZERO,
					GlStateManager.DestFactor	.ONE
			);

			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.blitToScreen(
					WINDOW.getWidth	(),
					WINDOW.getHeight(),
					false
			);

			RenderSystem										.disableBlend		();
			RenderSystem										.defaultBlendFunc	();
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET	.clear				(ON_OSX);
			MAIN_TARGET											.bindWrite			(false);
		}
	}
}

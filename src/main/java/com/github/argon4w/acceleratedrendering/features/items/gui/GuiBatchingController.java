package com.github.argon4w.acceleratedrendering.features.items.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.backends.states.FramebufferBindingState;
import com.github.argon4w.acceleratedrendering.core.backends.states.IBindingState;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleTextureTarget;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class GuiBatchingController {

	public static	final GuiBatchingController		INSTANCE = new GuiBatchingController();

	private			final Window					window;
	private			final IBindingState				viewport;
	private			final FramebufferBindingState	binding;
	private			final RenderTarget				overlay;

	private GuiBatchingController() {
		this.window		= Minecraft.getInstance()	.getWindow				();
		this.viewport	= CoreFeature				.createViewportState	();
		this.binding	= new FramebufferBindingState						();
		this.overlay	= new SimpleTextureTarget							(true);
	}

	public void startBatching() {
		if (		AcceleratedItemRenderingFeature	.isEnabled					()
				&&	AcceleratedItemRenderingFeature	.shouldAccelerateInGui		()
				&&	AcceleratedItemRenderingFeature	.shouldUseGuiItemBatching	()
				&&	CoreFeature						.isLoaded					()
		) {
			CoreFeature.setGuiBatching();
		}
	}

	public void flushBatching(GuiGraphics graphics) {
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

			overlay.blitToScreen(
					window.getWidth	(),
					window.getHeight(),
					false
			);

			RenderSystem.disableBlend		();
			RenderSystem.defaultBlendFunc	();
			binding		.record				();
			overlay		.clear				(Minecraft.ON_OSX);
			binding		.restore			();
		}
	}

	public void resize(
			int		newWidth,
			int		newHeight,
			boolean	clearError
	) {
		overlay.resize(
				newWidth,
				newHeight,
				clearError
		);
	}

	public void useOverlayTarget() {
		if (CoreFeature.isGuiBatching()) {
			viewport.record		();
			binding	.record		();
			overlay	.bindWrite	(false);
		}
	}

	public void resetOverlayTarget() {
		if (CoreFeature.isGuiBatching()) {
			overlay	.unbindWrite();
			binding	.restore	();
			viewport.restore	();
		}
	}
}

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
import org.lwjgl.opengl.GL46;

public class GuiBatchingController {

	public static	final GuiBatchingController	INSTANCE = new GuiBatchingController();

	private			final Window				window;
	private			final IBindingState			viewport;
	private			final IBindingState			scissorDraw;
	private			final IBindingState			scissorFlush;
	private			final IBindingState			binding;
	private			final RenderTarget			overlay;

	private GuiBatchingController() {
		this.window			= Minecraft.getInstance()	.getWindow				();
		this.viewport		= CoreFeature				.createViewportState	();
		this.scissorDraw	= CoreFeature				.createScissorState		();
		this.scissorFlush	= CoreFeature				.createScissorState		();
		this.binding		= new FramebufferBindingState						();
		this.overlay		= new SimpleTextureTarget							(true);
	}

	public void startBatching(GuiGraphics graphics) {
		if (		AcceleratedItemRenderingFeature	.isEnabled					()
				&&	AcceleratedItemRenderingFeature	.shouldAccelerateInGui		()
				&&	AcceleratedItemRenderingFeature	.shouldUseGuiItemBatching	()
				&&	CoreFeature						.isLoaded					()
		) {
			CoreFeature.setGuiBatching	();
			scissorDraw.record			(graphics);
		}
	}

	public void flushBatching(GuiGraphics graphics) {
		if (CoreFeature.isGuiBatching()) {
			scissorFlush						.record				(graphics);
			scissorDraw							.restore			();
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
			binding		.record				(graphics);
			overlay		.clear				(Minecraft.ON_OSX);
			binding		.restore			();
			scissorFlush.restore			();
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

	public void useOverlayTarget(GuiGraphics graphics) {
		if (CoreFeature.isGuiBatching()) {
			viewport.record		(graphics);
			binding	.record		(graphics);
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

	public void delete() {
		viewport	.delete			();
		scissorDraw	.delete			();
		scissorFlush.delete			();
		binding		.delete			();
		overlay		.destroyBuffers	();
	}
}

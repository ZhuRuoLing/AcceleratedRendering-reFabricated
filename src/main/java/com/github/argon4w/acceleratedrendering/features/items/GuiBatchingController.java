package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.opengl.GL46;

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
			GL46.glPushDebugGroup(GL46.GL_DEBUG_SOURCE_APPLICATION, 419, "Flush Batching");
			CoreFeature							.resetGuiBatching	();
			((IAcceleratedGuiGraphics) graphics).flushItemBatching	();

			RenderSystem.backupProjectionMatrix	();
			RenderSystem.enableBlend			();
			RenderSystem.blendFuncSeparate		(
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

			RenderSystem										.setShaderTexture		(0, AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.getColorTextureId());
			RenderSystem										.restoreProjectionMatrix();
			RenderSystem										.disableBlend			();
			RenderSystem										.defaultBlendFunc		();
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET	.clear					(ON_OSX);
			MAIN_TARGET											.bindWrite				(false);
			GL46.glPopDebugGroup();
		}
	}
}

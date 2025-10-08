package com.github.argon4w.acceleratedrendering.core.utils;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RenderTypeUtils {

	public static ResourceLocation getTextureLocation(RenderType renderType) {
		if (renderType == null) {
			return null;
		}

		if (!(renderType instanceof RenderType.CompositeRenderType composite)) {
			return null;
		}

		return composite
				.state
				.textureState
				.cutoutTexture()
				.orElse(null);
	}

	public static boolean isCulled(RenderType renderType) {
		if (renderType == null) {
			return false;
		}

		if (!(renderType instanceof RenderType.CompositeRenderType composite)) {
			return false;
		}

		return composite
				.state
				.cullState
				.enabled;
	}

	public static LayerDrawType getDrawType(RenderType renderType) {
		return renderType.sortOnUpload ? LayerDrawType.TRANSLUCENT : LayerDrawType.OPAQUE;
	}
}

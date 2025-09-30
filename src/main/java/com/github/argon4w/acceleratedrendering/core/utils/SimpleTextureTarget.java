package com.github.argon4w.acceleratedrendering.core.utils;

import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;

public class SimpleTextureTarget extends TextureTarget {

	public SimpleTextureTarget(boolean useDepth) {
		super(
				Minecraft.getInstance().getWindow().getWidth	(),
				Minecraft.getInstance().getWindow().getHeight	(),
				useDepth,
				Minecraft.ON_OSX
		);
	}
}

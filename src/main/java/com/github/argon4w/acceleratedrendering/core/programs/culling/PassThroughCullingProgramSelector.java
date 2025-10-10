package com.github.argon4w.acceleratedrendering.core.programs.culling;

import net.minecraft.client.renderer.RenderType;

public class PassThroughCullingProgramSelector implements ICullingProgramSelector {

	public static final ICullingProgramSelector	INSTANCE = new PassThroughCullingProgramSelector();

	@Override
	public ICullingProgramDispatcher select(RenderType renderType) {
		return switch (renderType.mode()) {
			case QUADS		-> PassThroughCullingProgramDispatcher.QUAD;
			case TRIANGLES	-> PassThroughCullingProgramDispatcher.TRIANGLE;
			default			-> throw new IllegalArgumentException("Unsupported mode: " + renderType.mode());
		};
	}
}

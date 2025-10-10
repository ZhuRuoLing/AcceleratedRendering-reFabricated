package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class OrientationCullingProgramSelector implements ICullingProgramSelector {

	private final ICullingProgramSelector	parent;
	private final ICullingProgramDispatcher	quadDispatcher;
	private final ICullingProgramDispatcher	triangleDispatcher;

	public OrientationCullingProgramSelector(
			ICullingProgramSelector	parent,
			ResourceLocation		quadProgramKey,
			ResourceLocation		triangleProgramKey
	) {
		this.parent				= parent;
		this.quadDispatcher		= new OrientationCullingProgramDispatcher(VertexFormat.Mode.QUADS,		quadProgramKey);
		this.triangleDispatcher	= new OrientationCullingProgramDispatcher(VertexFormat.Mode.TRIANGLES,	triangleProgramKey);
	}

	@Override
	public ICullingProgramDispatcher select(RenderType renderType) {
		if (			OrientationCullingFeature	.isEnabled				()
				&&	(	OrientationCullingFeature	.shouldIgnoreCullState	() || RenderTypeUtils.isCulled(renderType))
		) {
			return switch (renderType.mode()) {
				case QUADS		-> quadDispatcher;
				case TRIANGLES	-> triangleDispatcher;
				default			-> parent.select(renderType);
			};
		}

		return parent.select(renderType);
	}
}

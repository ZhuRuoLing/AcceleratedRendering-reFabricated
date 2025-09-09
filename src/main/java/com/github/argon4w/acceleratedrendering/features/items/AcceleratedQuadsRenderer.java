package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.features.items.contexts.AcceleratedQuadsRenderContext;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@ExtensionMethod({
		VertexConsumerExtension	.class,
		BakedModelExtension		.class
})
public class AcceleratedQuadsRenderer implements IAcceleratedRenderer<AcceleratedQuadsRenderContext> {

	public static final AcceleratedQuadsRenderer INSTANCE = new AcceleratedQuadsRenderer();

	@Override
	public void render(
			VertexConsumer					vertexConsumer,
			AcceleratedQuadsRenderContext	context,
			Matrix4f						transform,
			Matrix3f						normal,
			int								light,
			int								overlay,
			int								color
	) {
		var extension	= vertexConsumer.getAccelerated	();
		var quads		= context		.quads			();
		var colors		= context		.colors			();

		extension.beginTransform(transform, normal);

		for (var quad : quads) {
			quad
					.getAccelerated	()
					.renderFast		(
							transform,
							normal,
							extension,
							light,
							overlay,
							colors.getColor(quad.getTintIndex())
					);
		}

		extension.endTransform();
	}
}

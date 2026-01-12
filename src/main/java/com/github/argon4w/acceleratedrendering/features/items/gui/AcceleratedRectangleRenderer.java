package com.github.argon4w.acceleratedrendering.features.items.gui;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.features.items.gui.contexts.RectangleDrawContext;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@ExtensionMethod(VertexConsumerExtension.class)
public class AcceleratedRectangleRenderer implements IAcceleratedRenderer<RectangleDrawContext> {

	public static final AcceleratedRectangleRenderer INSTANCE = new AcceleratedRectangleRenderer();

	@Override
	public void render(
			VertexConsumer			vertexConsumer,
			RectangleDrawContext	context,
			Matrix4f				transform,
			Matrix3f				normal,
			int						light,
			int						overlay,
			int						color
	) {
		var extension = vertexConsumer.getAccelerated();

		extension.beginTransform(transform, normal);

		var minX = context.minX();
		var minY = context.minY();
		var maxX = context.maxX();
		var maxY = context.maxY();

		vertexConsumer.addVertex(minX, minY, 0).setColor(color);
		vertexConsumer.addVertex(minX, maxY, 0).setColor(color);
		vertexConsumer.addVertex(maxX, maxY, 0).setColor(color);
		vertexConsumer.addVertex(maxX, minY, 0).setColor(color);

		extension.endTransform();
	}
}

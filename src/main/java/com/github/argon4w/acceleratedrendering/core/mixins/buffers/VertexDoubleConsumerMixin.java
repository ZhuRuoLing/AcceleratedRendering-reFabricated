package com.github.argon4w.acceleratedrendering.core.mixins.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@ExtensionMethod(VertexConsumerExtension.class)
@Mixin			(targets = "com.mojang.blaze3d.vertex.VertexMultiConsumer$Double")
public class VertexDoubleConsumerMixin implements IAcceleratedVertexConsumer {

	@Shadow @Final private VertexConsumer first;
	@Shadow @Final private VertexConsumer second;

	@Unique
	@Override
	public boolean isAccelerated() {
		return 		first	.getAccelerated().isAccelerated()
				&&	second	.getAccelerated().isAccelerated();
	}

	@Unique
	@Override
	public <T>  void doRender(
			IAcceleratedRenderer<T>	renderer,
			T						context,
			Matrix4f				transform,
			Matrix3f				normal,
			int						light,
			int						overlay,
			int						color
	) {
		first
				.getAccelerated	()
				.doRender		(
						renderer,
						context,
						transform,
						normal,
						light,
						overlay,
						color
				);
		second
				.getAccelerated	()
				.doRender		(
						renderer,
						context,
						transform,
						normal,
						light,
						overlay,
						color
				);
	}
}

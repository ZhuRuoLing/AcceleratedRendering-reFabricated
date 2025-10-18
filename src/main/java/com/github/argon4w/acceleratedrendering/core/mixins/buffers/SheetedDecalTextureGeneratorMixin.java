package com.github.argon4w.acceleratedrendering.core.mixins.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.SheetedDecalTextureRenderer;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@ExtensionMethod(VertexConsumerExtension		.class)
@Mixin			(SheetedDecalTextureGenerator	.class)
public class SheetedDecalTextureGeneratorMixin implements IAcceleratedVertexConsumer {

	@Shadow @Final private VertexConsumer	delegate;
	@Shadow @Final private Matrix4f			cameraInversePose;
	@Shadow @Final private Matrix3f			normalInversePose;
	@Shadow @Final private float			textureScale;

	@Unique
	@Override
	public boolean isAccelerated() {
		return delegate
				.getAccelerated()
				.isAccelerated();
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
		delegate
				.getAccelerated	()
				.doRender		(
						new SheetedDecalTextureRenderer<>(
								renderer,
								cameraInversePose,
								normalInversePose,
								textureScale
						),
						context,
						transform,
						normal,
						light,
						overlay,
						color
				);
	}
}

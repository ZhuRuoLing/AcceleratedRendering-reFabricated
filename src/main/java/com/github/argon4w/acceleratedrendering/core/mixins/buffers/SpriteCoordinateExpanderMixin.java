package com.github.argon4w.acceleratedrendering.core.mixins.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.AcceleratedSpriteCoordinateExpander;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.DecoratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@ExtensionMethod(VertexConsumerExtension	.class)
@Mixin			(SpriteCoordinateExpander	.class)
public class SpriteCoordinateExpanderMixin implements IAcceleratedVertexConsumer {

	@Shadow @Final private VertexConsumer		delegate;
	@Shadow @Final private TextureAtlasSprite	sprite;

	@Unique
	@Override
	public VertexConsumer decorate(VertexConsumer buffer) {
		return new AcceleratedSpriteCoordinateExpander(buffer, sprite);
	}

	@Unique
	@Override
	public boolean isAccelerated() {
		return delegate
				.getAccelerated	()
				.isAccelerated	();
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
						new DecoratedRenderer<>(renderer, this),
						context,
						transform,
						normal,
						light,
						overlay,
						color
				);
	}
}

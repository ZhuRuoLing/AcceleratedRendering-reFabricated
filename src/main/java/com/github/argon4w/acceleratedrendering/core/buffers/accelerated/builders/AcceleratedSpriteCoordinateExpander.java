package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@AllArgsConstructor
@EqualsAndHashCode	(callSuper = false)
@ExtensionMethod	(VertexConsumerExtension.class)
public class AcceleratedSpriteCoordinateExpander extends AcceleratedVertexConsumerWrapper {

	private final VertexConsumer		delegate;
	private final TextureAtlasSprite	sprite;

	@Override
	public VertexConsumer getDelegate() {
		return delegate;
	}

	@Override
	public VertexConsumer decorate(VertexConsumer buffer) {
		return new AcceleratedSpriteCoordinateExpander(
				getDelegate				()
						.getAccelerated	()
						.decorate		(buffer),
				sprite
		);
	}

	@Override
	public VertexConsumer uv(float pU, float pV) {
		delegate.uv(
				sprite.getU(pU * 16.0f),
				sprite.getV(pV * 16.0f)
		);
		return this;
	}

	@Override
	public void vertex(
			float	pX,
			float	pY,
			float	pZ,
			float	red,
			float	green,
			float	blue,
			float	alpha,
			float	pU,
			float	pV,
			int		pPackedOverlay,
			int		pPackedLight,
			float	pNormalX,
			float	pNormalY,
			float	pNormalZ
	) {
		delegate.vertex(
				pX,
				pY,
				pZ,
				red,
				green,
				blue,
				alpha,
				sprite.getU(pU * 16.0f),
				sprite.getV(pV * 16.0f),
				pPackedOverlay,
				pPackedLight,
				pNormalX,
				pNormalY,
				pNormalZ
		);
	}
}

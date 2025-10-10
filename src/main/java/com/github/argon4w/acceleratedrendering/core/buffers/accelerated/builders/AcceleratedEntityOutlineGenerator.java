package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;

import java.nio.ByteBuffer;


@AllArgsConstructor
@EqualsAndHashCode	(callSuper = false)
@ExtensionMethod	(VertexConsumerExtension.class)
public class AcceleratedEntityOutlineGenerator extends AcceleratedVertexConsumerWrapper {

	private final VertexConsumer	delegate;
	private final int				color;

	@Override
	public VertexConsumer getDelegate() {
		return delegate;
	}

	@Override
	public VertexConsumer decorate(VertexConsumer buffer) {
		return new AcceleratedEntityOutlineGenerator(
				getDelegate				()
						.getAccelerated	()
						.decorate		(buffer),
				color
		);
	}

	@Override
	public void addClientMesh(
			ByteBuffer meshBuffer,
			int			size,
			int			color,
			int			light,
			int			overlay
	) {
		getDelegate				()
				.getAccelerated	()
				.addClientMesh	(
						meshBuffer,
						size,
						this.color,
						light,
						overlay
				);
	}

	@Override
	public void addServerMesh(
			ServerMesh serverMesh,
			int			color,
			int			light,
			int			overlay
	) {
		getDelegate				()
				.getAccelerated	()
				.addServerMesh	(
						serverMesh,
						this.color,
						light,
						overlay
				);
	}

	@Override
	public VertexConsumer vertex(
			double pX,
			double pY,
			double pZ
	) {
		delegate.vertex(
				pX,
				pY,
				pZ
		).color(color);
		return this;
	}

	@Override
	public VertexConsumer color(
			int pRed,
			int pGreen,
			int pBlue,
			int pAlpha
	) {
		return this;
	}

	@Override
	public VertexConsumer overlayCoords(int pU, int pV) {
		return this;
	}

	@Override
	public VertexConsumer uv2(int pU, int pV) {
		return this;
	}

	@Override
	public VertexConsumer normal(
			float pNormalX,
			float pNormalY,
			float pNormalZ
	) {
		return this;
	}

	@Override
	public void vertex(
			float	x,
			float	y,
			float	z,
			float	red,
			float	green,
			float	blue,
			float	alpha,
			float	u,
			float	v,
			int		packedOverlay,
			int		packedLight,
			float	normalX,
			float	normalY,
			float	normalZ
	) {
		getDelegate().vertex(
				x,
				y,
				z,
				red,
				green,
				blue,
				alpha,
				u,
				v,
				packedOverlay,
				packedLight,
				normalX,
				normalY,
				normalZ
		);
	}
}

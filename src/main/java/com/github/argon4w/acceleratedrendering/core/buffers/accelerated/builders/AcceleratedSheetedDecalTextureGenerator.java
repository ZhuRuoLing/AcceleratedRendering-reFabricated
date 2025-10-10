package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

@ExtensionMethod	(VertexConsumerExtension.class)
@EqualsAndHashCode	(
		onlyExplicitlyIncluded	= true,
		callSuper				= false
)
public class AcceleratedSheetedDecalTextureGenerator extends AcceleratedVertexConsumerWrapper {

	@EqualsAndHashCode.Include private	final	VertexConsumer	delegate;
	@EqualsAndHashCode.Include private	final	Matrix4f		cameraInverse;
	private                             final	Matrix3f		normalInverse;
	private								final	float			textureScale;

	private								final	Vector3f		cachedCamera;
	private								final	Vector3f		cachedNormal;

	private										float			vertexX;
	private										float			vertexY;
	private										float			vertexZ;

	private										float			normalX;
	private										float			normalY;
	private										float			normalZ;

	public AcceleratedSheetedDecalTextureGenerator(
			VertexConsumer	delegate,
			Matrix4f		cameraInverse,
			Matrix3f		normalInverse,
			float			textureScale
	) {
		this.delegate		= delegate;
		this.cameraInverse	= cameraInverse;
		this.normalInverse	= normalInverse;
		this.textureScale	= textureScale;

		this.cachedCamera	= new Vector3f();
		this.cachedNormal	= new Vector3f();

		this.vertexX		= 0;
		this.vertexY		= 0;
		this.vertexZ		= 0;

		this.normalX		= 0;
		this.normalY		= 0;
		this.normalZ		= 0;
	}

	@Override
	protected VertexConsumer getDelegate() {
		return delegate;
	}

	@Override
	public VertexConsumer decorate(VertexConsumer buffer) {
		return new AcceleratedSheetedDecalTextureGenerator(
				getDelegate				()
						.getAccelerated	()
						.decorate		(buffer),
				cameraInverse,
				normalInverse,
				textureScale
		);
	}

	@Override
	public void addClientMesh(
			ByteBuffer	meshBuffer,
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
						-1,
						light,
						overlay
				);
	}

	@Override
	public void addServerMesh(
			ServerMesh	serverMesh,
			int			color,
			int			light,
			int			overlay
	) {
		getDelegate				()
				.getAccelerated	()
				.addServerMesh	(
						serverMesh,
						-1,
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
		vertexX = (float) pX;
		vertexY = (float) pY;
		vertexZ = (float) pZ;

		delegate.vertex(
				pX,
				pY,
				pZ
		);
		return this;
	}

	@Override
	public VertexConsumer uv(float pU, float pV) {
		return this;
	}

	@Override
	public VertexConsumer color(
			int pRed,
			int pGreen,
			int pBlue,
			int pAlpha
	) {
		delegate.color(-1);
		return this;
	}

	@Override
	public VertexConsumer normal(
			float pNormalX,
			float pNormalY,
			float pNormalZ
	) {
		this.normalX = pNormalX;
		this.normalY = pNormalY;
		this.normalZ = pNormalZ;

		delegate.normal(
				pNormalX,
				pNormalY,
				pNormalZ
		);
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
		this
				.vertex			(x,			y,			z)
				.color			(red,		green,		blue, alpha)
				.uv				(u,			v)
				.overlayCoords	(packedOverlay)
				.uv2			(packedLight)
				.normal			(normalX,	normalY,	normalZ)
				.endVertex		();
	}

	@Override
	public void endVertex() {
		var normal		= normalInverse.transform(
				normalX,
				normalY,
				normalZ,
				cachedNormal
		);

		var camera		= cameraInverse.transformPosition(
				vertexX,
				vertexY,
				vertexZ,
				cachedCamera
		);

		var direction	= Direction.getNearest(
				normal.x(),
				normal.y(),
				normal.z()
		);

		camera	.rotateY	((float) 	Math.PI);
		camera	.rotateX	((float) (-	Math.PI / 2));
		camera	.rotate		(direction.getRotation());

		delegate.uv			(-camera.x() * textureScale, -camera.y() * textureScale);
		super	.endVertex	();
	}
}

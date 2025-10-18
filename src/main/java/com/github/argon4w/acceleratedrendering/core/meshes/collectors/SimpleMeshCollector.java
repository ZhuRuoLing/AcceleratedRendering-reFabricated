package com.github.argon4w.acceleratedrendering.core.meshes.collectors;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryInterface;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryLayout;
import com.github.argon4w.acceleratedrendering.core.meshes.identity.IMeshData;
import com.github.argon4w.acceleratedrendering.core.utils.PackedVector2i;
import com.github.argon4w.acceleratedrendering.core.utils.Vertex;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import lombok.Getter;
import net.minecraft.util.FastColor;

public class SimpleMeshCollector implements VertexConsumer, IMeshCollector {

	@Getter private	final	IMemoryLayout<VertexFormatElement>	layout;
	@Getter private	final	ByteBufferBuilder					buffer;
	@Getter private final	Vertex								vertex;
	@Getter private	final	IMeshData							data;

	private			final	long								vertexSize;
	private			final	IMemoryInterface					posOffset;
	private			final	IMemoryInterface					colorOffset;
	private			final	IMemoryInterface					uv0Offset;
	private			final	IMemoryInterface					uv2Offset;
	private			final	IMemoryInterface					normalOffset;

	@Getter private			long								vertexAddress;
	@Getter private			int									vertexCount;

	public SimpleMeshCollector(IMemoryLayout<VertexFormatElement> layout) {
		this.layout			= layout;
		this.buffer			= new ByteBufferBuilder			(1024);
		this.vertex			= new Vertex					();
		this.data			= CoreFeature	.createMeshData	(layout);

		this.vertexSize		= this.layout	.getSize		();
		this.posOffset		= this.layout	.getElement		(VertexFormatElement.POSITION);
		this.colorOffset	= this.layout	.getElement		(VertexFormatElement.COLOR);
		this.uv0Offset		= this.layout	.getElement		(VertexFormatElement.UV);
		this.uv2Offset		= this.layout	.getElement		(VertexFormatElement.UV2);
		this.normalOffset	= this.layout	.getElement		(VertexFormatElement.NORMAL);

		this.vertexAddress	= -1L;
		this.vertexCount	= 0;
	}

	@Override
	public void flush() {
		data.addVertex(vertex);
	}

	@Override
	public VertexConsumer addVertex(
			float pX,
			float pY,
			float pZ
	) {
		if (vertexCount != 0) {
			data.addVertex(vertex);
		}

		vertexCount ++;
		vertexAddress = buffer.reserve((int) vertexSize);

		posOffset.putFloat(vertexAddress + 0L, pX);
		posOffset.putFloat(vertexAddress + 4L, pY);
		posOffset.putFloat(vertexAddress + 8L, pZ);

		var vertexPosition	= vertex.getPosition();

		vertexPosition.x	= pX;
		vertexPosition.y	= pY;
		vertexPosition.z	= pZ;

		return this;
	}

	@Override
	public VertexConsumer setColor(
			int pRed,
			int pGreen,
			int pBlue,
			int pAlpha
	) {
		if (vertexAddress == -1) {
			throw new IllegalStateException("Vertex not building!");
		}

		colorOffset.putByte(vertexAddress + 0L, (byte) pRed);
		colorOffset.putByte(vertexAddress + 1L, (byte) pGreen);
		colorOffset.putByte(vertexAddress + 2L, (byte) pBlue);
		colorOffset.putByte(vertexAddress + 3L, (byte) pAlpha);

		var vertexColor	= vertex.getColor();

		vertexColor.x	= pRed;
		vertexColor.y	= pGreen;
		vertexColor.z	= pBlue;
		vertexColor.w	= pAlpha;

		return this;
	}

	@Override
	public VertexConsumer setUv(float pU, float pV) {
		if (vertexAddress == -1) {
			throw new IllegalStateException("Vertex not building!");
		}

		uv0Offset.putFloat(vertexAddress + 0L, pU);
		uv0Offset.putFloat(vertexAddress + 4L, pV);

		var vertexUv	= vertex.getUv();

		vertexUv.x		= pU;
		vertexUv.y		= pV;

		return this;
	}

	@Override
	public VertexConsumer setUv1(int pU, int pV) {
		return this;
	}

	@Override
	public VertexConsumer setUv2(int pU, int pV) {
		if (vertexAddress == -1) {
			throw new IllegalStateException("Vertex not building!");
		}

		uv2Offset.putShort(vertexAddress + 0L, (short) pU);
		uv2Offset.putShort(vertexAddress + 2L, (short) pV);

		var vertexLight	= vertex.getLight();

		vertexLight.x	= pU;
		vertexLight.y	= pV;

		return this;
	}

	@Override
	public VertexConsumer setNormal(
			float pNormalX,
			float pNormalY,
			float pNormalZ
	) {
		if (vertexAddress == -1) {
			throw new IllegalStateException("Vertex not building!");
		}

		normalOffset.putNormal(vertexAddress + 0L, pNormalX);
		normalOffset.putNormal(vertexAddress + 1L, pNormalY);
		normalOffset.putNormal(vertexAddress + 2L, pNormalZ);

		var vertexNormal	= vertex.getNormal();

		vertexNormal.x		= pNormalX;
		vertexNormal.y		= pNormalY;
		vertexNormal.z		= pNormalZ;

		return this;
	}

	@Override
	public void addVertex(
			float	pX,
			float	pY,
			float	pZ,
			int		pColor,
			float	pU,
			float	pV,
			int		pPackedOverlay,
			int		pPackedLight,
			float	pNormalX,
			float	pNormalY,
			float	pNormalZ
	) {
		vertexCount++;
		vertexAddress = buffer.reserve((int) vertexSize);

		posOffset	.putFloat	(vertexAddress + 0L,	pX);
		posOffset	.putFloat	(vertexAddress + 4L,	pY);
		posOffset	.putFloat	(vertexAddress + 8L,	pZ);
		colorOffset	.putInt		(vertexAddress,			FastColor.ABGR32.fromArgb32(pColor));
		uv0Offset	.putFloat	(vertexAddress + 0L,	pU);
		uv0Offset	.putFloat	(vertexAddress + 4L,	pV);
		uv2Offset	.putInt		(vertexAddress,			pPackedLight);
		normalOffset.putNormal	(vertexAddress + 0L,	pNormalX);
		normalOffset.putNormal	(vertexAddress + 1L,	pNormalY);
		normalOffset.putNormal	(vertexAddress + 2L,	pNormalZ);

		data.addVertex(
				pX,
				pY,
				pZ,
				pU,
				pV,
				FastColor.ARGB32.red	(pColor),
				FastColor.ARGB32.green	(pColor),
				FastColor.ARGB32.blue	(pColor),
				FastColor.ARGB32.alpha	(pColor),
				PackedVector2i	.unpackU(pPackedLight),
				PackedVector2i	.unpackV(pPackedLight),
				pNormalX,
				pNormalY,
				pNormalZ
		);
	}
}

package com.github.argon4w.acceleratedrendering.core.meshes.collectors;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryInterface;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryLayout;
import com.github.argon4w.acceleratedrendering.core.utils.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.meshes.data.IMeshData;
import com.github.argon4w.acceleratedrendering.core.utils.PackedVector2i;
import com.github.argon4w.acceleratedrendering.core.utils.Vertex;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import lombok.Getter;
import net.minecraft.util.FastColor;

public class SimpleMeshCollector extends DefaultedVertexConsumer implements IMeshCollector {

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
		this.posOffset		= this.layout	.getElement		(DefaultVertexFormat.ELEMENT_POSITION);
		this.colorOffset	= this.layout	.getElement		(DefaultVertexFormat.ELEMENT_COLOR);
		this.uv0Offset		= this.layout	.getElement		(DefaultVertexFormat.ELEMENT_UV);
		this.uv2Offset		= this.layout	.getElement		(DefaultVertexFormat.ELEMENT_UV2);
		this.normalOffset	= this.layout	.getElement		(DefaultVertexFormat.ELEMENT_NORMAL);

		this.vertexAddress	= -1L;
		this.vertexCount	= 0;
	}

	@Override
	public void flush() {
		data.addVertex(vertex);
	}

	@Override
	public void endVertex() {

	}

	@Override
	public VertexConsumer vertex(
			double pX,
			double pY,
			double pZ
	) {
		if (vertexCount != 0) {
			data.addVertex(vertex);
		}

		vertexCount ++;
		vertexAddress = buffer.reserve((int) vertexSize);

		posOffset.putFloat(vertexAddress + 0L, (float) pX);
		posOffset.putFloat(vertexAddress + 4L, (float) pY);
		posOffset.putFloat(vertexAddress + 8L, (float) pZ);

		var vertexPosition	= vertex.getPosition();

		vertexPosition.x	= (float) pX;
		vertexPosition.y	= (float) pY;
		vertexPosition.z	= (float) pZ;

		return this;
	}

	@Override
	public VertexConsumer color(
			int pRed,
			int pGreen,
			int pBlue,
			int pAlpha
	) {
		if (vertexAddress == -1) {
			throw new IllegalStateException("Vertex not building!");
		}

		if (defaultColorSet) {
			pRed	= defaultR;
			pGreen	= defaultG;
			pBlue	= defaultB;
			pAlpha	= defaultA;
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
	public VertexConsumer uv(float pU, float pV) {
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
	public VertexConsumer overlayCoords(int pU, int pV) {
		return this;
	}

	@Override
	public VertexConsumer uv2(int pU, int pV) {
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
	public VertexConsumer normal(
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
		if (defaultColorSet) {
			red		= defaultR / 255.0f;
			green	= defaultG / 255.0f;
			blue	= defaultB / 255.0f;
			alpha	= defaultA / 255.0f;
		}

		vertexCount++;
		vertexAddress = buffer.reserve((int) vertexSize);

		posOffset	.putFloat	(vertexAddress + 0L,	pX);
		posOffset	.putFloat	(vertexAddress + 4L,	pY);
		posOffset	.putFloat	(vertexAddress + 8L,	pZ);
		colorOffset	.putInt		(vertexAddress,			FastColor.ABGR32.color(
				(int) (alpha	* 255.0f),
				(int) (blue		* 255.0f),
				(int) (green	* 255.0f),
				(int) (red		* 255.0f)
		));
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
				(int) (red		* 255.0f),
				(int) (green	* 255.0f),
				(int) (blue		* 255.0f),
				(int) (alpha	* 255.0f),
				PackedVector2i.unpackU(pPackedLight),
				PackedVector2i.unpackV(pPackedLight),
				pNormalX,
				pNormalY,
				pNormalZ
		);
	}
}

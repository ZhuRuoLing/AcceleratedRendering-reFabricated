package com.github.argon4w.acceleratedrendering.features.text;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.collectors.SimpleMeshCollector;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

@ExtensionMethod(VertexConsumerExtension.class)
public class AcceleratedBakedGlyphRenderer implements IAcceleratedRenderer<Vector2f> {

	private static final Matrix4f TRANSFORM	= new Matrix4f();
	private static final Matrix3f NORMAL	= new Matrix3f();

	private final Map<IBufferGraph, IMesh>	meshes;
	private final BakedGlyph				bakedGlyph;
	private final boolean					italic;

	public AcceleratedBakedGlyphRenderer(BakedGlyph bakedGlyph, boolean italic) {
		this.meshes		= new Object2ObjectOpenHashMap<>();
		this.bakedGlyph	= bakedGlyph;
		this.italic		= italic;
	}

	@Override
	public void render(
			VertexConsumer	vertexConsumer,
			Vector2f		context,
			Matrix4f		transform,
			Matrix3f		normal,
			int				light,
			int				overlay,
			int				color
	) {
		var extension	= vertexConsumer.getAccelerated	();
		var mesh		= meshes		.get			(extension);

		TRANSFORM.set		(transform);
		TRANSFORM.translate	(
				context.x,
				context.y,
				0.0f
		);

		extension.beginTransform(TRANSFORM, NORMAL);

		if (mesh != null) {
			mesh.write(
					extension,
					color,
					light,
					overlay
			);

			extension.endTransform();
			return;
		}

		var meshCollector		= new SimpleMeshCollector	(extension.getLayout());
		var meshBuilder			= extension.decorate		(meshCollector);

		var italicOffsetUp		= italic ? 1.0f - 0.25f * bakedGlyph.up		: 0.0f;
		var italicOffsetDown	= italic ? 1.0f - 0.25f * bakedGlyph.down	: 0.0f;

		var positions = new Vector2f[] {
				new Vector2f(bakedGlyph.left	+ italicOffsetUp,	bakedGlyph.up),
				new Vector2f(bakedGlyph.left	+ italicOffsetDown,	bakedGlyph.down),
				new Vector2f(bakedGlyph.right	+ italicOffsetDown,	bakedGlyph.down),
				new Vector2f(bakedGlyph.right	+ italicOffsetUp,	bakedGlyph.up)
		};

		var texCoords = new Vector2f[] {
				new Vector2f(bakedGlyph.u0, bakedGlyph.v0),
				new Vector2f(bakedGlyph.u0, bakedGlyph.v1),
				new Vector2f(bakedGlyph.u1, bakedGlyph.v1),
				new Vector2f(bakedGlyph.u1, bakedGlyph.v0),
		};

		for (var i = 0; i < 4; i ++) {
			var position = new Vector3f(positions[i], 0.0f);
			var texCoord = texCoords[i];

			meshBuilder
					.vertex	(
							position.x,
							position.y,
							position.z
					)
					.color	(-1)
					.uv		(texCoord.x, texCoord.y)
					.uv2	(0);
		}

		mesh = AcceleratedTextRenderingFeature
				.getMeshType()
				.getBuilder	()
				.build		(meshCollector);

		meshes	.put	(extension, mesh);
		mesh	.write	(
				extension,
				color,
				light,
				overlay
		);

		extension.endTransform();
	}
}

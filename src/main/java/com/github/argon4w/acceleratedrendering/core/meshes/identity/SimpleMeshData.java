package com.github.argon4w.acceleratedrendering.core.meshes.identity;

import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryLayout;
import com.github.argon4w.acceleratedrendering.core.utils.Vertex;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class SimpleMeshData implements IMeshData {

	private final List			<Vertex>				vertices;
	private final IMemoryLayout	<VertexFormatElement>	layout;

	public SimpleMeshData(IMemoryLayout<VertexFormatElement> layout) {
		this.vertices	= new ObjectArrayList<>();
		this.layout		= layout;
	}

	@Override
	public void addVertex(Vertex vertex) {
		var position	= vertex.getPosition();
		var texCoord	= vertex.getUv		();
		var color		= vertex.getColor	();
		var light		= vertex.getLight	();
		var normal		= vertex.getNormal	();

		addVertex(
				position.x,
				position.y,
				position.z,
				texCoord.x,
				texCoord.y,
				color.x,
				color.y,
				color.z,
				color.w,
				light.x,
				light.y,
				normal.x,
				normal.y,
				normal.z
		);
	}

	@Override
	public void addVertex(
			float	posX,
			float	posY,
			float	posZ,
			float	texU,
			float	texV,
			int		colorR,
			int		colorG,
			int		colorB,
			int		colorA,
			int		lightU,
			int		lightV,
			float	normalX,
			float	normalY,
			float	normalZ
	) {
		var vertex = new Vertex();

		vertex.getPosition().x = posX;
		vertex.getPosition().y = posY;
		vertex.getPosition().z = posZ;

		vertex.getUv()		.x = texU;
		vertex.getUv()		.y = texV;

		vertex.getColor()	.w = colorA;
		vertex.getColor()	.x = colorR;
		vertex.getColor()	.y = colorG;
		vertex.getColor()	.z = colorB;

		vertex.getLight()	.x = lightU;
		vertex.getLight()	.y = lightV;

		vertex.getNormal()	.x = normalX;
		vertex.getNormal()	.y = normalY;
		vertex.getNormal()	.z = normalZ;

		vertices.add(vertex);
	}
}

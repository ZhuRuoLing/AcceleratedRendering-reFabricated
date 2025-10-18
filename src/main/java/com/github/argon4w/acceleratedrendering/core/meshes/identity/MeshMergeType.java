package com.github.argon4w.acceleratedrendering.core.meshes.identity;

import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryLayout;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public enum MeshMergeType {

	IGNORED,
	MERGED;

	public IMeshData create(IMemoryLayout<VertexFormatElement> layout) {
		return create(this, layout);
	}

	public static IMeshData create(MeshMergeType type, IMemoryLayout<VertexFormatElement> layout) {
		return switch (type) {
			case IGNORED	-> new IdentityMeshData	();
			case MERGED		-> new SimpleMeshData	(layout);
		};
	}
}

package com.github.argon4w.acceleratedrendering.core.meshes.collectors;

import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryLayout;
import com.github.argon4w.acceleratedrendering.core.utils.ByteBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.meshes.identity.IMeshData;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public interface IMeshCollector {

	IMeshData							getData			();
	ByteBufferBuilder					getBuffer		();
	IMemoryLayout<VertexFormatElement>	getLayout		();
	int									getVertexCount	();
	void								flush			();
}

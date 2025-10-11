package com.github.argon4w.acceleratedrendering.core.buffers.blocks;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache.BlockBufferBindingCacheType;

public enum BlockBufferBindingRestorerType {

	IGNORED,
	RESTORED;

	public IBlockBufferBindingRestorer create(BufferBlockType blockType, int bindingRange) {
		return create(
				this,
				blockType,
				CoreFeature.getBlockBufferBindingCacheType(),
				bindingRange
		);
	}

	public static IBlockBufferBindingRestorer create(
			BlockBufferBindingRestorerType	restorerType,
			BufferBlockType					blockType,
			BlockBufferBindingCacheType		cacheType,
			int								bindingRange
	) {
		return switch (restorerType) {
			case IGNORED	-> EmptyBlockBufferBindingRestorer.INSTANCE;
			case RESTORED	-> new SimpleBlockBufferBindingRestorer(
					cacheType,
					bindingRange,
					blockType.getBindingBlock	(),
					blockType.getBufferParam	(),
					blockType.getOffsetParam	(),
					blockType.getSizeParam		()
			);
		};
	}
}

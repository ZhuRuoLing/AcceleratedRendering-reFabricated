package com.github.argon4w.acceleratedrendering.core.buffers.blocks.states;

import com.github.argon4w.acceleratedrendering.core.buffers.blocks.BufferBlockType;
import com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache.BlockBufferBindingCacheType;

public enum BlockBufferBindingStateType {

	IGNORED,
	RESTORED;

	public IBlockBufferBindingState create(
			BlockBufferBindingCacheType	cacheType,
			BufferBlockType				blockType,
			int							bindingRange
	) {
		return create(
				this,
				cacheType,
				blockType,
				bindingRange
		);
	}

	public static IBlockBufferBindingState create(
			BlockBufferBindingStateType		stateType,
			BlockBufferBindingCacheType		cacheType,
			BufferBlockType					blockType,
			int								bindingRange
	) {
		return switch (stateType) {
			case IGNORED	-> EmptyBlockBufferBindingState.INSTANCE;
			case RESTORED	-> new SimpleBlockBufferBindingState(
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

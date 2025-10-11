package com.github.argon4w.acceleratedrendering.core.buffers.blocks.states;

import com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache.BlockBufferBindingCacheType;
import com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache.IBlockBufferBindingCache;

import static org.lwjgl.opengl.GL46.*;

public class SimpleBlockBufferBindingState implements IBlockBufferBindingState {

	private final IBlockBufferBindingCache	bindingCache;
	private final int						bindingRange;
	private final int						bindingBlock;
	private final int						bufferParam;
	private final int						offsetParam;
	private final int						sizeParam;

	public SimpleBlockBufferBindingState(
			BlockBufferBindingCacheType	type,
			int							bindingRange,
			int							bindingBlock,
			int							bufferParam,
			int							offsetParam,
			int							sizeParam
	) {
		this.bindingRange	= bindingRange;
		this.bindingCache	= type.create(this.bindingRange);

		this.bindingBlock	= bindingBlock;
		this.bufferParam	= bufferParam;
		this.offsetParam	= offsetParam;
		this.sizeParam		= sizeParam;
	}

	@Override
	public void delete() {
		bindingCache.delete();
	}

	@Override
	public void record() {
		for (int bindingPoint = 0; bindingPoint < bindingRange; bindingPoint++) {
			bindingCache.setup(
					bindingPoint,
					glGetIntegeri(bufferParam,	bindingPoint),
					glGetIntegeri(offsetParam,	bindingPoint),
					glGetIntegeri(sizeParam,	bindingPoint)
			);
		}
	}

	@Override
	public void restore() {
		for (int bindingPoint = 0; bindingPoint < bindingRange; bindingPoint ++) {
			var buffer	= bindingCache.getBuffer(bindingPoint);
			var offset	= bindingCache.getOffset(bindingPoint);
			var size	= bindingCache.getSize	(bindingPoint);

			if (		offset	== 0
					&&	size	== 0
			) {
				glBindBufferBase(
						bindingBlock,
						bindingPoint,
						buffer
				);
			} else {
				glBindBufferRange(
						bindingBlock,
						bindingPoint,
						buffer,
						offset,
						size
				);
			}
		}
	}
}

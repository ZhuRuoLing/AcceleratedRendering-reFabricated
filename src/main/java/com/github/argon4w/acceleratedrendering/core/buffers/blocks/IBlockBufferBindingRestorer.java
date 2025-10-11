package com.github.argon4w.acceleratedrendering.core.buffers.blocks;

public interface IBlockBufferBindingRestorer {

	void delete();
	void record();
	void restore();
}

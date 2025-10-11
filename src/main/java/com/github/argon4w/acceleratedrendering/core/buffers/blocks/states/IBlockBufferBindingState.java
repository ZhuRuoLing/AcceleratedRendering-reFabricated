package com.github.argon4w.acceleratedrendering.core.buffers.blocks.states;

public interface IBlockBufferBindingState {

	void delete();
	void record();
	void restore();
}

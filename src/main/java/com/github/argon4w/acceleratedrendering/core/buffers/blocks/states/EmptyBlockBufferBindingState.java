package com.github.argon4w.acceleratedrendering.core.buffers.blocks.states;

public class EmptyBlockBufferBindingState implements IBlockBufferBindingState {

	public static final IBlockBufferBindingState INSTANCE = new EmptyBlockBufferBindingState();

	@Override
	public void delete() {

	}

	@Override
	public void record() {

	}

	@Override
	public void restore() {

	}
}

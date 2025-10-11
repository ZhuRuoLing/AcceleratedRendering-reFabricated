package com.github.argon4w.acceleratedrendering.core.buffers.blocks;

public class EmptyBlockBufferBindingRestorer implements IBlockBufferBindingRestorer {

	public static final IBlockBufferBindingRestorer INSTANCE = new EmptyBlockBufferBindingRestorer();

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

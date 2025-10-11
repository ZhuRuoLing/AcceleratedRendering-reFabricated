package com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache;

public enum BlockBufferBindingCacheType {

	SIMPLE,
	UNSAFE,
	HANDLE;

	public IBlockBufferBindingCache create(int size) {
		return create(this, size);
	}

	public static IBlockBufferBindingCache create(BlockBufferBindingCacheType type, int size) {
		return switch (type) {
			case SIMPLE -> new SimpleBlockBufferBindingCache		(size);
			case HANDLE -> new FlattenBlockBufferBindingCache		(size);
			case UNSAFE -> new UnsafeMemoryBlockBufferBindingCache	(size);
		};
	}
}

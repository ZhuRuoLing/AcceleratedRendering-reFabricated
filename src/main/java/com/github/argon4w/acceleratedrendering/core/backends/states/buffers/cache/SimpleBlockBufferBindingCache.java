package com.github.argon4w.acceleratedrendering.core.backends.states.buffers.cache;

public class SimpleBlockBufferBindingCache implements IBlockBufferBindingCache {

	private final SimpleBlockBufferBinding[] cache;

	public SimpleBlockBufferBindingCache(int size) {
		cache = new SimpleBlockBufferBinding[size];

		for (int i = 0; i < cache.length; i++) {
			cache[i] = new SimpleBlockBufferBinding();
		}
	}

	@Override
	public void delete() {

	}

	@Override
	public void setup(
			int bindingPoint,
			int buffer,
			long offset,
			long size
	) {
		cache[bindingPoint].setupBlockBufferBinding(
				buffer,
				offset,
				size
		);
	}

	@Override
	public int getBuffer(int bindingPoint) {
		return cache[bindingPoint].getBuffer();
	}

	@Override
	public long getOffset(int bindingPoint) {
		return cache[bindingPoint].getOffset();
	}

	@Override
	public long getSize(int bindingPoint) {
		return cache[bindingPoint].getSize();
	}
}

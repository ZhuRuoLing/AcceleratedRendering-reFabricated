package com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class FlattenBlockBufferBindingCache implements IBlockBufferBindingCache {

	public static	final VarHandle	HANDLE						= MethodHandles.arrayElementVarHandle(long[].class).withInvokeExactBehavior();
	public static	final int		BLOCK_BUFFER_BINDING_SIZE	= 3;
	public static	final int		BUFFER_OFFSET				= 0;
	public static	final int		OFFSET_OFFSET				= 1;
	public static	final int		SIZE_OFFSET					= 2;

	private			final long[]	cache;

	public FlattenBlockBufferBindingCache(int size) {
		this.cache = new long[size * BLOCK_BUFFER_BINDING_SIZE];
	}

	@Override
	public void delete() {

	}

	@Override
	public void setup(
			int		bindingPoint,
			int		buffer,
			long	offset,
			long	size
	) {
		var bindingIndex = bindingPoint * BLOCK_BUFFER_BINDING_SIZE;

		HANDLE.set(cache, bindingIndex + BUFFER_OFFSET,	(long)	buffer);
		HANDLE.set(cache, bindingIndex + OFFSET_OFFSET,			offset);
		HANDLE.set(cache, bindingIndex + SIZE_OFFSET,			size);
	}

	@Override
	public int getBuffer(int bindingPoint) {
		return (int) (long) HANDLE.get(cache, bindingPoint * BLOCK_BUFFER_BINDING_SIZE + BUFFER_OFFSET);
	}

	@Override
	public long getOffset(int bindingPoint) {
		return (long) HANDLE.get(cache, bindingPoint * BLOCK_BUFFER_BINDING_SIZE + OFFSET_OFFSET);
	}

	@Override
	public long getSize(int bindingPoint) {
		return (long) HANDLE.get(cache, bindingPoint * BLOCK_BUFFER_BINDING_SIZE + SIZE_OFFSET);
	}
}

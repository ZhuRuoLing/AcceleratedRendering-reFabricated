package com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import sun.misc.Unsafe;

public class UnsafeMemoryBlockBufferBindingCache implements IBlockBufferBindingCache {

	public static	final Unsafe	UNSAFE							= UnsafeAccess.UNSAFE;
	public static	final long		BLOCK_BUFFER_BINDING_SIZE		= 3L * 8L;
	public static	final long		BUFFER_OFFSET					= 0L * 8L;
	public static	final long		OFFSET_OFFSET					= 1L * 8L;
	public static	final long		SIZE_OFFSET						= 2L * 8L;

	private			final long		address;

	public UnsafeMemoryBlockBufferBindingCache(int size) {
		this.address = UNSAFE.allocateMemory(size * BLOCK_BUFFER_BINDING_SIZE);
	}

	@Override
	public void delete() {
		UNSAFE.freeMemory(address);
	}

	@Override
	public void setup(
			int		bindingPoint,
			int		buffer,
			long	offset,
			long	size
	) {
		var bindingAddress = address + bindingPoint * BLOCK_BUFFER_BINDING_SIZE;

		UNSAFE.putInt	(bindingAddress + BUFFER_OFFSET,	buffer);
		UNSAFE.putLong	(bindingAddress + OFFSET_OFFSET,	offset);
		UNSAFE.putLong	(bindingAddress + SIZE_OFFSET,		size);
	}

	@Override
	public int getBuffer(int bindingPoint) {
		return UNSAFE.getInt(address + bindingPoint * BLOCK_BUFFER_BINDING_SIZE + BUFFER_OFFSET);
	}

	@Override
	public long getOffset(int bindingPoint) {
		return UNSAFE.getLong(address + bindingPoint * BLOCK_BUFFER_BINDING_SIZE + OFFSET_OFFSET);
	}

	@Override
	public long getSize(int bindingPoint) {
		return UNSAFE.getLong(address + bindingPoint * BLOCK_BUFFER_BINDING_SIZE + SIZE_OFFSET);
	}
}

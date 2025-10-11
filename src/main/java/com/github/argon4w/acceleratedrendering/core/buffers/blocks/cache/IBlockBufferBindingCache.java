package com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache;

public interface IBlockBufferBindingCache {

	void	delete			();
	void	setup			(int bindingPoint, int buffer, long offset, long size);
	int		getBuffer		(int bindingPoint);
	long	getOffset		(int bindingPoint);
	long	getSize			(int bindingPoint);
}

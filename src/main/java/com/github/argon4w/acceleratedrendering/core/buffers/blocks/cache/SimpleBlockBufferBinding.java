package com.github.argon4w.acceleratedrendering.core.buffers.blocks.cache;

import lombok.Getter;

@Getter
public class SimpleBlockBufferBinding {

	private int		buffer;
	private long	offset;
	private long	size;

	public SimpleBlockBufferBinding() {
		this.buffer	= -1;
		this.offset	= -1;
		this.size	= -1;
	}

	public void setupBlockBufferBinding(
			int		buffer,
			long	offset,
			long	size
	) {
		this.buffer	= buffer;
		this.offset	= offset;
		this.size	= size;
	}
}

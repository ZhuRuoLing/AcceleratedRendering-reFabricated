package com.github.argon4w.acceleratedrendering.core.backends.buffers;

import java.nio.ByteBuffer;

public interface IServerBuffer {

	int getBufferHandle ();
	void delete			();
	void data			(ByteBuffer	data);
	void bind           (int		target);
	void bindBase       (int		target, int index);
	void bindRange      (int		target, int index, long offset, long size);
}

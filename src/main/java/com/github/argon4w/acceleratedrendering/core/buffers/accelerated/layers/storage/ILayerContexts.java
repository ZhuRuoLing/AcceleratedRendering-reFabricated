package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.DrawContextPool;

public interface ILayerContexts extends Iterable<DrawContextPool.DrawContext> {

	void	add		(DrawContextPool.DrawContext drawContext);
	void	reset	();
	void	prepare	();
	boolean	isEmpty	();
}

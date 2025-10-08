package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.DrawContextPool;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.Iterator;

public class SimpleLayerContexts implements ILayerContexts {

	protected final ReferenceArrayList<DrawContextPool.DrawContext> contexts;

	public SimpleLayerContexts(int size) {
		contexts = new ReferenceArrayList<>(size);
	}

	@Override
	public void add(DrawContextPool.DrawContext drawContext) {
		contexts.add(drawContext);
	}

	@Override
	public void reset() {
		contexts.clear();
	}

	@Override
	public void prepare() {

	}

	@Override
	public boolean isEmpty() {
		return contexts.isEmpty();
	}

	@Override
	public Iterator<DrawContextPool.DrawContext> iterator() {
		return contexts.iterator();
	}
}

package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.sorted;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.ILayerContexts;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.ILayerStorage;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.SimpleLayerContexts;

public class SortedLayerStorage implements ILayerStorage {

	private final SimpleLayerContexts contexts;

	public SortedLayerStorage(int size) {
		this.contexts = new SortedLayerContexts(size);
	}

	@Override
	public ILayerContexts get(LayerDrawType type) {
		return contexts;
	}

	@Override
	public void reset() {
		contexts.reset();
	}
}

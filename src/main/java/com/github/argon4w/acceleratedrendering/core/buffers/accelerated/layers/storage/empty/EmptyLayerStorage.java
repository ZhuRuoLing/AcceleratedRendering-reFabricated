package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.empty;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.ILayerContexts;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.ILayerStorage;

public class EmptyLayerStorage implements ILayerStorage {

	public static final EmptyLayerStorage INSTANCE = new EmptyLayerStorage();

	@Override
	public ILayerContexts get(LayerDrawType type) {
		return EmptyLayerContexts.INSTANCE;
	}

	@Override
	public void reset() {

	}
}

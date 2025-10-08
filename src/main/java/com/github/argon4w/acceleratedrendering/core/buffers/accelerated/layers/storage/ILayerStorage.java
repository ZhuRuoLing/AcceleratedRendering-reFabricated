package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;

public interface ILayerStorage {

	ILayerContexts	get		(LayerDrawType type);
	void			reset	();
}

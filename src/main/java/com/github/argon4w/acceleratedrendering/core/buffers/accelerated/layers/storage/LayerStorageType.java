package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.sorted.SortedLayerStorage;

public enum LayerStorageType {

	SORTED,
	SEPARATED;

	public static ILayerStorage create(LayerStorageType type, int size) {
		return switch (type) {
			case SORTED		-> new SortedLayerStorage	(size);
			case SEPARATED	-> new SeparatedLayerStorage(size);
		};
	}
}

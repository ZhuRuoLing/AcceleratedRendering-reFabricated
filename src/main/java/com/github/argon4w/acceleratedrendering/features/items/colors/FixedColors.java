package com.github.argon4w.acceleratedrendering.features.items.colors;

public record FixedColors(int color) implements ILayerColors {

	@Override
	public int getColor(int layer) {
		return color;
	}
}

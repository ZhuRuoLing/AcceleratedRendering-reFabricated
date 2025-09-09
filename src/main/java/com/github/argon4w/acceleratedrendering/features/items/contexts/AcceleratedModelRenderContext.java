package com.github.argon4w.acceleratedrendering.features.items.contexts;

import com.github.argon4w.acceleratedrendering.features.items.colors.ILayerColors;
import net.minecraft.util.RandomSource;

public record AcceleratedModelRenderContext(
		RandomSource randomSource,
		ILayerColors layerColors
) {

}

package com.github.argon4w.acceleratedrendering.features.items.contexts;

import com.github.argon4w.acceleratedrendering.features.items.colors.ILayerColors;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public record AcceleratedQuadsRenderContext(List<BakedQuad> quads, ILayerColors colors) {

}

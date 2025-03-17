/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;

/**
 * Transformer for {@link BakedQuad baked quads}.
 *
 * @see QuadTransformers
 */
public interface IQuadTransformer {
    int STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
    int POSITION = findOffset(VertexFormatElement.POSITION);
    int COLOR = findOffset(VertexFormatElement.COLOR);
    int UV0 = findOffset(VertexFormatElement.UV0);
    int UV1 = findOffset(VertexFormatElement.UV1);
    int UV2 = findOffset(VertexFormatElement.UV2);
    int NORMAL = findOffset(VertexFormatElement.NORMAL);

    void processInPlace(BakedQuad quad);

    default void processInPlace(List<BakedQuad> quads) {
        for (BakedQuad quad : quads)
            processInPlace(quad);
    }

    private static int findOffset(VertexFormatElement element) {
        if (DefaultVertexFormat.BLOCK.contains(element)) {
            // Divide by 4 because we want the int offset
            return DefaultVertexFormat.BLOCK.getOffset(element) / 4;
        }
        return -1;
    }
}

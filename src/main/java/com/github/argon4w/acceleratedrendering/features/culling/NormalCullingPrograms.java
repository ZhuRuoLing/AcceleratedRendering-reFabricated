package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.gl.programs.BarrierFlags;
import com.github.argon4w.acceleratedrendering.core.programs.LoadComputeShaderEvent;
import com.github.argon4w.acceleratedrendering.core.programs.culling.LoadCullingProgramSelectorEvent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class NormalCullingPrograms {

    public static final ResourceLocation CORE_POS_TEX_COLOR_QUAD_CULLING_KEY = AcceleratedRenderingModEntry.location("core_pos_tex_color_quad_culling");
    public static final ResourceLocation CORE_POS_TEX_COLOR_TRIANGLE_CULLING_KEY = AcceleratedRenderingModEntry.location("core_pos_tex_color_triangle_culling");
    public static final ResourceLocation CORE_ENTITY_QUAD_CULLING_KEY = AcceleratedRenderingModEntry.location("core_entity_quad_culling");
    public static final ResourceLocation CORE_ENTITY_TRIANGLE_CULLING_KEY = AcceleratedRenderingModEntry.location("core_entity_triangle_culling");

    @SubscribeEvent
    public static void onLoadComputeShaders(LoadComputeShaderEvent event) {
        event.loadComputeShader(
                CORE_ENTITY_QUAD_CULLING_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/culling/entity_quad_culling_shader.compute"),
                BarrierFlags.SHADER_STORAGE,
                BarrierFlags.ATOMIC_COUNTER
        );

        event.loadComputeShader(
                CORE_ENTITY_TRIANGLE_CULLING_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/culling/entity_triangle_culling_shader.compute"),
                BarrierFlags.SHADER_STORAGE,
                BarrierFlags.ATOMIC_COUNTER
        );

        event.loadComputeShader(
                CORE_POS_TEX_COLOR_QUAD_CULLING_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/culling/pos_tex_color_quad_culling_shader.compute"),
                BarrierFlags.SHADER_STORAGE,
                BarrierFlags.ATOMIC_COUNTER
        );

        event.loadComputeShader(
                CORE_POS_TEX_COLOR_TRIANGLE_CULLING_KEY,
                AcceleratedRenderingModEntry.location("shaders/core/culling/pos_tex_color_triangle_culling_shader.compute"),
                BarrierFlags.SHADER_STORAGE,
                BarrierFlags.ATOMIC_COUNTER
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLoadCullingPrograms(LoadCullingProgramSelectorEvent event) {
        event.loadFor(DefaultVertexFormat.NEW_ENTITY, parent -> new NormalCullingProgramSelector(
                parent,
                VertexFormat.Mode.TRIANGLES,
                CORE_ENTITY_TRIANGLE_CULLING_KEY
        ));

        event.loadFor(DefaultVertexFormat.NEW_ENTITY, parent -> new NormalCullingProgramSelector(
                parent,
                VertexFormat.Mode.QUADS,
                CORE_ENTITY_QUAD_CULLING_KEY
        ));

        event.loadFor(DefaultVertexFormat.POSITION_TEX_COLOR, parent -> new NormalCullingProgramSelector(
                parent,
                VertexFormat.Mode.TRIANGLES,
                CORE_POS_TEX_COLOR_TRIANGLE_CULLING_KEY
        ));

        event.loadFor(DefaultVertexFormat.POSITION_TEX_COLOR, parent -> new NormalCullingProgramSelector(
                parent,
                VertexFormat.Mode.QUADS,
                CORE_POS_TEX_COLOR_QUAD_CULLING_KEY
        ));
    }
}

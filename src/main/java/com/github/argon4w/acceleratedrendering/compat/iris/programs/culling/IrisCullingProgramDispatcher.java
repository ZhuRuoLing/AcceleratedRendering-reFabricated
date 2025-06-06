package com.github.argon4w.acceleratedrendering.compat.iris.programs.culling;

import com.github.argon4w.acceleratedrendering.core.backends.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.backends.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.dispatchers.IPolygonProgramDispatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.resources.ResourceLocation;

public class IrisCullingProgramDispatcher implements IPolygonProgramDispatcher {

    private static final int GROUP_SIZE = 128;

    private final VertexFormat.Mode mode;
    private final ComputeProgram program;
    private final Uniform viewMatrixUniform;
    private final Uniform projectMatrixUniform;
    private final Uniform polygonCountUniform;
    private final Uniform vertexOffsetUniform;

    public IrisCullingProgramDispatcher(VertexFormat.Mode mode, ResourceLocation key) {
        this.mode = mode;
        this.program = ComputeShaderProgramLoader.getProgram(key);
        this.viewMatrixUniform = program.getUniform("viewMatrix");
        this.projectMatrixUniform = this.program.getUniform("projectMatrix");
        this.polygonCountUniform = program.getUniform("polygonCount");
        this.vertexOffsetUniform = program.getUniform("vertexOffset");
    }

    @Override
    public int dispatch(AcceleratedBufferBuilder builder) {
        int vertexCount = builder.getVertexCount();
        int vertexOffset = builder.getVertexOffset();
        int polygonCount = vertexCount / mode.primitiveLength;

        viewMatrixUniform.uploadMatrix4f(ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? ShadowRenderer.MODELVIEW : RenderSystem.getModelViewMatrix());
        projectMatrixUniform.uploadMatrix4f(ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? ShadowRenderer.PROJECTION : RenderSystem.getProjectionMatrix());
        polygonCountUniform.uploadUnsignedInt(polygonCount);
        vertexOffsetUniform.uploadUnsignedInt(vertexOffset);

        program.useProgram();
        program.dispatch((polygonCount + GROUP_SIZE - 1) / GROUP_SIZE);
        program.resetProgram();

        return program.getBarrierFlags();
    }
}

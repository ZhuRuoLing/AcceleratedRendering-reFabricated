package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.SheetedDecalTextureRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(SheetedDecalTextureGenerator.class)
public class SheetedDecalTextureGeneratorMixin implements IAcceleratedVertexConsumer {

    @Shadow @Final private VertexConsumer delegate;
    @Shadow @Final private Matrix4f cameraInversePose;
    @Shadow @Final private Matrix3f normalInversePose;
    @Shadow @Final private float textureScale;

    @Unique
    @Override
    public VertexConsumer decorate(VertexConsumer buffer) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public void beginTransform(Matrix4f transform, Matrix3f normal) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public void endTransform() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public boolean isAccelerated() {
        return ((IAcceleratedVertexConsumer) delegate).isAccelerated();
    }

    @Unique
    @Override
    public IBufferGraph getBufferGraph() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public RenderType getRenderType() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public void addClientMesh(
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public void addServerMesh(
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Unique
    @Override
    public <T>  void doRender(
            IAcceleratedRenderer<T> renderer,
            T context,
            Matrix4f transform,
            Matrix3f normal,
            int light,
            int overlay,
            int color
    ) {
        ((IAcceleratedVertexConsumer) delegate).doRender(
                new SheetedDecalTextureRenderer<>(
                        renderer,
                        cameraInversePose,
                        normalInversePose,
                        textureScale
                ),
                context,
                transform,
                normal,
                light,
                overlay,
                color
        );
    }
}

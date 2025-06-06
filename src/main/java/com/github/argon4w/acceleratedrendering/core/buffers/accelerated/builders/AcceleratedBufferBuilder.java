package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSetPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.ElementBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.MappedBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.VertexBufferPool;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.BlankBufferGraph;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.programs.extras.IExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.utils.MemUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class AcceleratedBufferBuilder implements IAcceleratedVertexConsumer, VertexConsumer {

    private final VertexBufferPool.VertexBuffer vertexBuffer;
    private final MappedBufferPool.Pooled varyingBuffer;
    private final ElementBufferPool.ElementSegment elementSegment;
    private final AcceleratedBufferSetPool.BufferSet bufferSet;

    private final IBufferGraph bufferGraph;
    private final RenderType renderType;
    private final VertexFormat.Mode mode;
    private final long vertexSize;
    private final int polygonSize;
    private final int polygonElementCount;

    private final long posOffset;
    private final long colorOffset;
    private final long uv0Offset;
    private final long uv1Offset;
    private final long uv2Offset;
    private final long normalOffset;

    private int elementCount;
    private int vertexCount;
    private long vertexAddress;
    private long transformAddress;
    private long normalAddress;
    private int activeSharing;
    private int cachedSharing;

    private Matrix4f cachedTransform;
    private Matrix3f cachedNormal;

    private final Matrix4f cachedTransformValue;
    private final Matrix3f cachedNormalValue;

    public AcceleratedBufferBuilder(
            VertexBufferPool.VertexBuffer vertexBuffer,
            MappedBufferPool.Pooled varyingBuffer,
            ElementBufferPool.ElementSegment elementSegment,
            AcceleratedBufferSetPool.BufferSet bufferSet,
            RenderType renderType
    ) {
        this.vertexBuffer = vertexBuffer;
        this.varyingBuffer = varyingBuffer;
        this.elementSegment = elementSegment;
        this.bufferSet = bufferSet;

        this.bufferGraph = new BlankBufferGraph(renderType);
        this.renderType = renderType;
        this.mode = this.renderType.mode;
        this.vertexSize = this.bufferSet.getVertexSize();
        this.polygonSize = this.mode.primitiveLength;
        this.polygonElementCount = this.mode.indexCount(this.polygonSize);

        this.posOffset = bufferSet.getOffset(VertexFormatElement.POSITION);
        this.colorOffset = bufferSet.getOffset(VertexFormatElement.COLOR);
        this.uv0Offset = bufferSet.getOffset(VertexFormatElement.UV0);
        this.uv1Offset = bufferSet.getOffset(VertexFormatElement.UV1);
        this.uv2Offset = bufferSet.getOffset(VertexFormatElement.UV2);
        this.normalOffset = bufferSet.getOffset(VertexFormatElement.NORMAL);

        this.elementCount = 0;
        this.vertexCount = 0;
        this.vertexAddress = -1;
        this.transformAddress = -1;
        this.normalAddress = -1;
        this.activeSharing = -1;
        this.cachedSharing = -1;

        this.cachedTransform = null;
        this.cachedNormal = null;

        this.cachedTransformValue = new Matrix4f();
        this.cachedNormalValue = new Matrix3f();
    }

    @Override
    public VertexConsumer addVertex(
            PoseStack.Pose pPose,
            float pX,
            float pY,
            float pZ
    ) {
        beginTransform(pPose.pose(), pPose.normal());
        return addVertex(
                pX,
                pY,
                pZ
        );
    }

    @Override
    public VertexConsumer addVertex(
            float pX,
            float pY,
            float pZ
    ) {
        long vertexAddress = vertexBuffer.reserve(vertexSize);
        long varyingAddress = varyingBuffer.reserve(4L * 4L);

        this.vertexAddress = vertexAddress;

        MemoryUtil.memPutFloat(vertexAddress + posOffset + 0L, pX);
        MemoryUtil.memPutFloat(vertexAddress + posOffset + 4L, pY);
        MemoryUtil.memPutFloat(vertexAddress + posOffset + 8L, pZ);

        MemoryUtil.memPutInt(varyingAddress + 0L * 4L, 0);
        MemoryUtil.memPutInt(varyingAddress + 1L * 4L, activeSharing);
        MemoryUtil.memPutInt(varyingAddress + 2L * 4L, -1);

        IExtraVertexData data = bufferSet.getExtraVertex(mode);
        data.addExtraVertex(vertexAddress);
        data.addExtraVarying(varyingAddress);

        vertexCount ++;
        elementCount ++;

        if (elementCount >= polygonSize) {
            elementSegment.countPolygons(polygonElementCount);
            elementCount = 0;
            activeSharing = -1;
        }

        return this;
    }

    @Override
    public VertexConsumer setColor(
            int pRed,
            int pGreen,
            int pBlue,
            int pAlpha
    ) {
        if (colorOffset == -1) {
            return this;
        }

        if (vertexAddress == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutByte(vertexAddress + colorOffset + 0L, (byte) pRed);
        MemoryUtil.memPutByte(vertexAddress + colorOffset + 1L, (byte) pGreen);
        MemoryUtil.memPutByte(vertexAddress + colorOffset + 2L, (byte) pBlue);
        MemoryUtil.memPutByte(vertexAddress + colorOffset + 3L, (byte) pAlpha);

        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        if (uv0Offset == -1) {
            return this;
        }

        if (vertexAddress == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutFloat(vertexAddress + uv0Offset + 0L, pU);
        MemoryUtil.memPutFloat(vertexAddress + uv0Offset + 4L, pV);

        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        if (uv1Offset == -1) {
            return this;
        }

        if (vertexAddress == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutShort(vertexAddress + uv1Offset + 0L, (short) pU);
        MemoryUtil.memPutShort(vertexAddress + uv1Offset + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        if (uv2Offset == -1) {
            return this;
        }

        if (vertexAddress == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemoryUtil.memPutShort(vertexAddress + uv2Offset + 0L, (short) pU);
        MemoryUtil.memPutShort(vertexAddress + uv2Offset + 2L, (short) pV);

        return this;
    }

    @Override
    public VertexConsumer setNormal(
            PoseStack.Pose pPose,
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        Matrix3f normal = pPose.normal();

        if (activeSharing == -1) {
            return VertexConsumer.super.setNormal(
                    pPose,
                    pNormalX,
                    pNormalY,
                    pNormalZ
            );
        }

        if (!normal.equals(cachedNormal)) {
            MemUtils.putMatrix3x4f(normalAddress, normal);
        }

        return setNormal(
                pNormalX,
                pNormalY,
                pNormalZ
        );
    }

    @Override
    public VertexConsumer setNormal(
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        if (normalOffset == -1) {
            return this;
        }

        if (vertexAddress == -1) {
            throw new IllegalStateException("Vertex not building!");
        }

        MemUtils.putNormal(vertexAddress + normalOffset + 0L, pNormalX);
        MemUtils.putNormal(vertexAddress + normalOffset + 1L, pNormalY);
        MemUtils.putNormal(vertexAddress + normalOffset + 2L, pNormalZ);

        return this;
    }

    @Override
    public void addVertex(
            float pX,
            float pY,
            float pZ,
            int pColor,
            float pU,
            float pV,
            int pPackedOverlay,
            int pPackedLight,
            float pNormalX,
            float pNormalY,
            float pNormalZ
    ) {
        long vertexAddress = vertexBuffer.reserve(vertexSize);
        long varyingAddress = varyingBuffer.reserve(4L * 4L);
        IExtraVertexData data = bufferSet.getExtraVertex(mode);

        data.addExtraVertex(vertexAddress);
        data.addExtraVarying(varyingAddress);

        MemoryUtil.memPutFloat(vertexAddress + posOffset + 0L, pX);
        MemoryUtil.memPutFloat(vertexAddress + posOffset + 4L, pY);
        MemoryUtil.memPutFloat(vertexAddress + posOffset + 8L, pZ);

        MemoryUtil.memPutInt(varyingAddress + 0L * 4L, 0);
        MemoryUtil.memPutInt(varyingAddress + 1L * 4L, activeSharing);
        MemoryUtil.memPutInt(varyingAddress + 2L * 4L, -1);

        if (colorOffset != -1) {
            MemoryUtil.memPutInt(vertexAddress + colorOffset + 0L, FastColor.ABGR32.fromArgb32(pColor));
        }

        if (uv0Offset != -1) {
            MemoryUtil.memPutFloat(vertexAddress + uv0Offset + 0L, pU);
            MemoryUtil.memPutFloat(vertexAddress + uv0Offset + 4L, pV);
        }

        if (uv1Offset != -1) {
            MemoryUtil.memPutInt(vertexAddress + uv1Offset + 0L, pPackedOverlay);
        }

        if (uv2Offset != -1) {
            MemoryUtil.memPutInt(vertexAddress + uv2Offset + 0L, pPackedLight);
        }

        if (normalOffset != -1) {
            MemUtils.putNormal(vertexAddress + normalOffset + 0L, pNormalX);
            MemUtils.putNormal(vertexAddress + normalOffset + 1L, pNormalY);
            MemUtils.putNormal(vertexAddress + normalOffset + 2L, pNormalZ);
        }

        vertexCount ++;
        elementCount ++;

        if (elementCount >= polygonSize) {
            elementSegment.countPolygons(polygonElementCount);
            elementCount = 0;
            activeSharing = -1;
        }
    }

    @Override
    public void beginTransform(Matrix4f transform, Matrix3f normal) {
        if (CoreFeature.shouldCacheIdenticalPose()
                && transform.equals(cachedTransform)
                && normal.equals(cachedNormal)
        ) {
            activeSharing = cachedSharing;
            return;
        }

        cachedTransform = cachedTransformValue.set(transform);
        cachedNormal = cachedNormalValue.set(normal);

        cachedSharing = bufferSet.getSharing();
        activeSharing = cachedSharing;

        transformAddress = bufferSet.reserveSharing();
        normalAddress = transformAddress + 4L * 4L * 4L;

        MemUtils.putMatrix4f(transformAddress, transform);
        MemUtils.putMatrix3x4f(normalAddress, normal);
    }

    @Override
    public void endTransform() {
        cachedTransform = null;
        cachedNormal = null;
        activeSharing = -1;
        cachedSharing = -1;
    }

    @Override
    public void addClientMesh(
            ByteBuffer meshBuffer,
            int size,
            int color,
            int light,
            int overlay
    ) {
        long bufferSize = vertexSize * size;
        long vertexAddress = vertexBuffer.reserve(bufferSize);
        long varyingAddress = varyingBuffer.reserve(4L * 4L * size);

        IExtraVertexData data = bufferSet.getExtraVertex(mode);
        data.addExtraVertex(vertexAddress);
        data.addExtraVarying(varyingAddress);

        MemoryUtil.memCopy(
                MemoryUtil.memAddress0(meshBuffer),
                vertexAddress,
                bufferSize
        );

        if (colorOffset != -1) {
            MemoryUtil.memPutInt(vertexAddress + colorOffset, FastColor.ABGR32.fromArgb32(color));
        }

        if (uv1Offset != -1) {
            MemoryUtil.memPutInt(vertexAddress + uv1Offset, overlay);
        }

        if (uv2Offset != -1) {
            MemoryUtil.memPutInt(vertexAddress + uv2Offset, light);
        }

        MemoryUtil.memPutInt(varyingAddress + 1L * 4L, activeSharing);
        MemoryUtil.memPutInt(varyingAddress + 2L * 4L, -1);

        for (int i = 0; i < size; i++) {
            MemoryUtil.memPutInt(varyingAddress + i * 4L * 4L, i);
        }

        elementSegment.countPolygons(mode.indexCount(size));
        vertexCount += size;
    }

    @Override
    public void addServerMesh(
            int offset,
            int size,
            int color,
            int light,
            int overlay
    ) {
        long meshOffset = offset / vertexSize;
        long vertexAddress = vertexBuffer.reserve(vertexSize * size);
        long varyingAddress = varyingBuffer.reserve(4L * 4L * size);

        IExtraVertexData data = bufferSet.getExtraVertex(mode);
        data.addExtraVertex(vertexAddress);
        data.addExtraVarying(varyingAddress);

        if (colorOffset != -1) {
            MemoryUtil.memPutInt(vertexAddress + colorOffset, FastColor.ABGR32.fromArgb32(color));
        }

        if (uv1Offset != -1) {
            MemoryUtil.memPutInt(vertexAddress + uv1Offset, overlay);
        }

        if (uv2Offset != -1) {
            MemoryUtil.memPutInt(vertexAddress + uv2Offset, light);
        }

        MemoryUtil.memPutInt(varyingAddress + 1L * 4L, activeSharing);
        MemoryUtil.memPutInt(varyingAddress + 2L * 4L, (int) meshOffset);

        for (int i = 0; i < size; i++) {
            MemoryUtil.memPutInt(varyingAddress + i * 4L * 4L, i);
        }

        elementSegment.countPolygons(mode.indexCount(size));
        vertexCount += size;
    }

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
        renderer.render(
                this,
                context,
                transform,
                normal,
                light,
                overlay,
                color
        );
    }

    @Override
    public VertexConsumer decorate(VertexConsumer buffer) {
        return buffer;
    }

    @Override
    public boolean isAccelerated() {
        return true;
    }

    @Override
    public IBufferGraph getBufferGraph() {
        return bufferGraph;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    public boolean isEmpty() {
        return vertexCount == 0;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVertexOffset() {
        return vertexBuffer.getOffset();
    }

    public VertexBufferPool.VertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public MappedBufferPool.Pooled getVaryingBuffer() {
        return varyingBuffer;
    }

    public ElementBufferPool.ElementSegment getElementSegment() {
        return elementSegment;
    }
}

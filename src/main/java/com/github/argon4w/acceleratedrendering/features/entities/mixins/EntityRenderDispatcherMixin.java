package com.github.argon4w.acceleratedrendering.features.entities.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;<init>()V"))
    private static void beginShadowTransform(
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            Entity pEntity,
            float pWeight,
            float pPartialTicks,
            LevelReader pLevel,
            float pSize,
            CallbackInfo ci,
            @Local(index = 20) PoseStack.Pose pose,
            @Local(index = 21) VertexConsumer vertexConsumer
    ) {
        ((IAcceleratedVertexConsumer) vertexConsumer).beginTransform(pose.pose(), pose.normal());
    }

    @Inject(method = "renderShadow", at = @At("TAIL"))
    private static void endShadowTransform(
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            Entity pEntity,
            float pWeight,
            float pPartialTicks,
            LevelReader pLevel,
            float pSize,
            CallbackInfo ci,
            @Local(index = 21) VertexConsumer vertexConsumer
    ) {
        ((IAcceleratedVertexConsumer) vertexConsumer).endTransform();
    }

    @Inject(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;getU0()F"))
    public void beginFlameTransform(
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            Entity pEntity,
            Quaternionf pQuaternion,
            CallbackInfo ci,
            @Local(index = 15) PoseStack.Pose pose,
            @Local(index = 14) VertexConsumer vertexConsumer
    ) {
        ((IAcceleratedVertexConsumer) vertexConsumer).beginTransform(pose.pose(), pose.normal());
    }

    @Inject(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;fireVertex(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFF)V", ordinal = 3))
    public void endFlameTransform(
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            Entity pEntity,
            Quaternionf pQuaternion,
            CallbackInfo ci,
            @Local(index = 10) float i,
            @Local(index = 14) VertexConsumer vertexConsumer
    ) {
        if (i - 0.45 < 0) {
            ((IAcceleratedVertexConsumer) vertexConsumer).endTransform();
        }
    }

    @Inject(method = "shadowVertex", at = @At("HEAD"), cancellable = true)
    private static void fastShadowVertex(
            PoseStack.Pose pPose,
            VertexConsumer pConsumer,
            int pColor,
            float pOffsetX,
            float pOffsetY,
            float pOffsetZ,
            float pU,
            float pV,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) pConsumer;

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        if (!AcceleratedEntityRenderingFeature.shouldUseAcceleratedPipeline()) {
            return;
        }

        if (!extension.isAccelerated()) {
            return;
        }

        ci.cancel();
        pConsumer.addVertex(
                pOffsetX,
                pOffsetY,
                pOffsetZ,
                pColor,
                pU,
                pV,
                OverlayTexture.NO_OVERLAY,
                LightTexture.FULL_BRIGHT,
                0.0F,
                1.0F,
                0.0F
        );
    }

    @Inject(method = "fireVertex", at = @At("HEAD"), cancellable = true)
    private static void fastFlameVertex(
            PoseStack.Pose pMatrixEntry,
            VertexConsumer pBuffer,
            float pX,
            float pY,
            float pZ,
            float pTexU,
            float pTexV,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) pBuffer;

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        if (!AcceleratedEntityRenderingFeature.shouldUseAcceleratedPipeline()) {
            return;
        }

        if (!extension.isAccelerated()) {
            return;
        }

        ci.cancel();
        pBuffer.addVertex(
                pX,
                pY,
                pZ,
                -1,
                pTexU,
                pTexV,
                OverlayTexture.NO_OVERLAY,
                LightTexture.FULL_BLOCK,
                0.0F,
                1.0F,
                0.0F
        );
    }
}

package com.github.argon4w.acceleratedrendering.compat.trinkets.mixins;

import com.github.argon4w.acceleratedrendering.compat.trinkets.TrinketsCompatFeature;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.filter.FilterFeature;
import com.github.argon4w.acceleratedrendering.features.text.AcceleratedTextRenderingFeature;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.TrinketFeatureRenderer;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrinketFeatureRenderer.class)
public class TrinketFeatureRendererMixin {
    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At("HEAD")
    )
    public void startRenderTrinketsLayer(
        PoseStack matrixStack,
        MultiBufferSource renderTypeBuffer,
        int light,
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        if (TrinketsCompatFeature.isEnabled()
            && !TrinketsCompatFeature.shouldAccelerateTrinkets()
        ) {
            AcceleratedEntityRenderingFeature.useVanillaPipeline();
            AcceleratedTextRenderingFeature.useVanillaPipeline();
        }
    }

    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At("TAIL")
    )
    public void stopRenderTrinketsLayer(
        PoseStack matrixStack,
        MultiBufferSource renderTypeBuffer,
        int light,
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        if (TrinketsCompatFeature.isEnabled()
            && !TrinketsCompatFeature.shouldAccelerateTrinkets()
        ) {
            AcceleratedEntityRenderingFeature.resetPipeline();
            AcceleratedTextRenderingFeature.resetPipeline();
        }
    }

    @WrapOperation(
        method = "lambda$render$0",
        at = @At(value = "INVOKE", target = "Ldev/emi/trinkets/api/client/TrinketRenderer;render(Lnet/minecraft/world/item/ItemStack;Ldev/emi/trinkets/api/SlotReference;Lnet/minecraft/client/model/EntityModel;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
    )
    public void filterTrinketsItem(
        TrinketRenderer instance,
        ItemStack itemStack,
        SlotReference slotContext,
        EntityModel<? extends LivingEntity> renderLayerParent,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int light,
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        Operation<Void> original
    ) {
        var pass = !FilterFeature.isEnabled()
            || !TrinketsCompatFeature.isEnabled()
            || !TrinketsCompatFeature.shouldFilterTrinketsItems()
            || TrinketsCompatFeature.testTrinketsItem(itemStack);

        if (!pass) {
            AcceleratedEntityRenderingFeature.useVanillaPipeline();
            AcceleratedTextRenderingFeature.useVanillaPipeline();
        }

        original.call(
            instance,
            itemStack,
            slotContext,
            renderLayerParent,
            poseStack,
            bufferSource,
            light,
            livingEntity,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch
        );

        if (!pass) {
            AcceleratedEntityRenderingFeature.resetPipeline();
            AcceleratedTextRenderingFeature.resetPipeline();
        }
    }
}

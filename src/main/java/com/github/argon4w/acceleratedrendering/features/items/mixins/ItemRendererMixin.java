package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.mixins.MCItemColorsAccessor;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.EmptyItemColor;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedQuad;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private static final Direction[] DIRECTIONS = Direction.values();

    @SuppressWarnings("deprecation")
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"))
    public void renderFast(
            ItemRenderer instance,
            BakedModel pModel,
            ItemStack pStack,
            int pCombinedLight,
            int pCombinedOverlay,
            PoseStack pPoseStack,
            VertexConsumer pBuffer,
            Operation<Void> original
    ) {
        IAcceleratedVertexConsumer extension1 = (IAcceleratedVertexConsumer) pBuffer;
        IAcceleratedBakedModel extension2 = (IAcceleratedBakedModel) pModel;

        if (!AcceleratedItemRenderingFeature.isEnabled()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        if (!AcceleratedItemRenderingFeature.shouldUseAcceleratedPipeline()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        if (!extension1.isAccelerated()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        if (extension2.isAccelerated()) {
            extension2.renderItemFast(
                    pStack,
                    pPoseStack,
                    extension1,
                    pCombinedLight,
                    pCombinedOverlay
            );
            return;
        }

        if (!AcceleratedItemRenderingFeature.shouldBakeMeshForQuad()) {
            original.call(
                    instance,
                    pModel,
                    pStack,
                    pCombinedLight,
                    pCombinedOverlay,
                    pPoseStack,
                    pBuffer
            );
            return;
        }

        RandomSource source = RandomSource.create();
        PoseStack.Pose pose = pPoseStack.last();
        ItemColor itemColor = getItemColorOrDefault(pStack);

        extension1.beginTransform(pose.pose(), pose.normal());

        for (Direction direction : DIRECTIONS) {
            source.setSeed(42L);

            for (BakedQuad quad : pModel.getQuads(null, direction, source)) {
                ((IAcceleratedBakedQuad) quad).renderFast(
                        extension1,
                        pCombinedLight,
                        pCombinedOverlay,
                        itemColor.getColor(pStack, quad.getTintIndex())
                );
            }
        }

        source.setSeed(42L);

        for (BakedQuad quad : pModel.getQuads(null, null, source)) {
            ((IAcceleratedBakedQuad) quad).renderFast(
                    extension1,
                    pCombinedLight,
                    pCombinedOverlay,
                    itemColor.getColor(pStack, quad.getTintIndex())
            );
        }

        extension1.endTransform();
    }

    private static ItemColor getItemColorOrDefault(ItemStack itemStack){
        MCItemColorsAccessor accessor = (MCItemColorsAccessor) Minecraft.getInstance();
        int id = BuiltInRegistries.ITEM.getId(itemStack.getItem());
        ItemColor color = ((ItemColorsAccessor) (accessor.getItemColors())).getItemColors().byId(id);
        if (color == null)return EmptyItemColor.INSTANCE;
        return color;
    }
}

package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements IAcceleratedGuiGraphics {

	@Inject(
			method	= "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/renderer/entity/ItemRenderer;getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;"
			)
	)
	public void startRenderingGui(
			LivingEntity	entity,
			Level			level,
			ItemStack		stack,
			int				x,
			int				y,
			int				seed,
			int				guiOffset,
			CallbackInfo	ci
	) {
		CoreFeature.setRenderingGui();
	}

	@Inject(
			method	= "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
					shift	= At.Shift.AFTER
			)
	)
	public void stopRenderingGui(
			LivingEntity	entity,
			Level			level,
			ItemStack		stack,
			int				x,
			int				y,
			int				seed,
			int				guiOffset,
			CallbackInfo	ci
	) {
		CoreFeature.resetRenderingGui();

		if (!CoreFeature.isGuiBatching()) {
			flushItemBatching();
		}
	}

	@WrapOperation(
			method	= "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
			)
	)
	public void setupBatchingLayers(
			ItemRenderer		instance,
			ItemStack			itemStack,
			ItemDisplayContext	displayContext,
			boolean				leftHand,
			PoseStack			poseStack,
			MultiBufferSource	bufferSource,
			int					combinedLight,
			int					combinedOverlay,
			BakedModel			bakedModel,
			Operation<Void>		original
	) {
		var useFlatLight =	CoreFeature	.isGuiBatching	()
				&&		!	bakedModel	.usesBlockLight	();

		if (useFlatLight) {
			CoreFeature.forceSetDefaultLayer				(1);
			CoreFeature.forceSetDefaultLayerBeforeFunction	(Lighting::setupForFlatItems);
			CoreFeature.forceSetDefaultLayerAfterFunction	(Lighting::setupFor3DItems);
		}

		original.call(
				instance,
				itemStack,
				displayContext,
				leftHand,
				poseStack,
				bufferSource,
				combinedLight,
				combinedOverlay,
				bakedModel
		);

		if (useFlatLight) {
			CoreFeature.resetDefaultLayer				();
			CoreFeature.resetDefaultLayerBeforeFunction	();
			CoreFeature.resetDefaultLayerAfterFunction	();
		}
	}

	@Unique
	@Override
	public void flushItemBatching() {
		CoreBuffers.ENTITY				.drawBuffers();
		CoreBuffers.BLOCK				.drawBuffers();
		CoreBuffers.POS					.drawBuffers();
		CoreBuffers.POS_TEX				.drawBuffers();
		CoreBuffers.POS_TEX_COLOR		.drawBuffers();
		CoreBuffers.POS_COLOR_TEX_LIGHT	.drawBuffers();
	}
}

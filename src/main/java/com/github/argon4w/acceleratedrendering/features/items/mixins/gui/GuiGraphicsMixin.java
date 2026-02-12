package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.CoreStates;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.github.argon4w.acceleratedrendering.features.items.gui.GuiBatchingController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
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
		if (CoreFeature.isLoaded()) {
			CoreFeature.setRenderingGui();
		}
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

		if (	!	CoreFeature.isGuiBatching	()
				&&	CoreFeature.isLoaded		()
		) {
			flushItemBatching();
		}
	}

	@Inject(
			method	= "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
			at		= {
					@At(
							value	= "INVOKE",
							target	= "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
							shift	= At.Shift.BEFORE
					),
					@At(
							value	= "INVOKE",
							target	= "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V",
							shift	= At.Shift.BEFORE,
							ordinal	= 0
					),
					@At(
							value	= "INVOKE",
							target	= "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V",
							shift	= At.Shift.BEFORE,
							ordinal	= 2
					)
			}
	)
	public void startRenderDecorationPart(
			Font			font,
			ItemStack		stack,
			int				x,
			int				y,
			String			text,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.useOverlayTarget((GuiGraphics) (Object) this);
	}

		@Inject(
				method	= "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
				at		= {
						@At(
								value	= "INVOKE",
								target	= "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
								shift	= At.Shift.AFTER
						),
						@At(
								value	= "INVOKE",
								target	= "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V",
								shift	= At.Shift.AFTER,
								ordinal	= 1
						),
					@At(
							value	= "INVOKE",
							target	= "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V",
							shift	= At.Shift.AFTER,
							ordinal	= 2
					)
			}
	)
	public void stopRenderDecorationPart(
			Font			font,
			ItemStack		stack,
			int				x,
			int				y,
			String			text,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.resetOverlayTarget();
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
		CoreStates						.recordBuffers	();
		CoreBuffers.ENTITY				.prepareBuffers	();
		CoreBuffers.BLOCK				.prepareBuffers	();
		CoreBuffers.POS					.prepareBuffers	();
		CoreBuffers.POS_TEX				.prepareBuffers	();
		CoreBuffers.POS_TEX_COLOR		.prepareBuffers	();
		CoreBuffers.POS_COLOR_TEX_LIGHT	.prepareBuffers	();
		CoreStates						.restoreBuffers	();

		CoreBuffers.ENTITY				.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.BLOCK				.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS					.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS_TEX				.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS_TEX_COLOR		.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS_COLOR_TEX_LIGHT	.drawBuffers	(LayerDrawType.ALL);

		CoreBuffers.ENTITY				.clearBuffers	();
		CoreBuffers.BLOCK				.clearBuffers	();
		CoreBuffers.POS					.clearBuffers	();
		CoreBuffers.POS_TEX				.clearBuffers	();
		CoreBuffers.POS_TEX_COLOR		.clearBuffers	();
		CoreBuffers.POS_COLOR_TEX_LIGHT	.clearBuffers	();
	}
}

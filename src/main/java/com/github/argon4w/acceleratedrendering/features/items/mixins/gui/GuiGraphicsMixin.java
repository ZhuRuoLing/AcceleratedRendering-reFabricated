package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreBuffers;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.CoreStates;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.LayerDrawType;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.github.argon4w.acceleratedrendering.features.items.gui.GuiBatchingController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings	("UnstableApiUsage")
@Mixin				(GuiGraphics.class)
public class GuiGraphicsMixin implements IAcceleratedGuiGraphics {

	@WrapOperation(
			method	= "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"
			)
	)
	public int renderDecorationStringsFast(
			GuiGraphics			instance,
			Font				font,
			String				text,
			int					textX,
			int					textY,
			int					textColor,
			boolean				dropShadow,
			Operation<Integer>	original
	) {
		if (!CoreFeature.isGuiBatching()) {
			return original.call(
					instance,
					font,
					text,
					textX,
					textY,
					textColor,
					dropShadow
			);
		}

		GuiBatchingController.INSTANCE.recordString(
				instance,
				font,
				text,
				textX,
				textY,
				textColor,
				dropShadow
		);
		return 0;
	}

	@WrapOperation(
			method	= "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V"
			)
	)
	public void renderDecorationRectanglesFast(
			GuiGraphics		instance,
			RenderType		renderType,
			int				minX,
			int				minY,
			int				maxX,
			int				maxY,
			int				color,
			Operation<Void>	original
	) {
		if (!CoreFeature.isGuiBatching()) {
			original.call(
					instance,
					renderType,
					minX,
					minY,
					maxX,
					maxY,
					color
			);
			return;
		}

		GuiBatchingController.INSTANCE.recordRectangle(
				instance,
				renderType,
				minX,
				minY,
				maxX,
				maxY,
				color
		);
	}

//	@WrapOperation(
//			method	= "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
//			at		= @At(
//					value	= "INVOKE",
//					target	= "Lnet/minecraftforge/client/ItemDecoratorHandler;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V"
//			),
//			remap	= false
//	)
//	public void renderDecorationCustomFast(
//			ItemDecoratorHandler	instance,
//			GuiGraphics				guiGraphics,
//			Font					font,
//			ItemStack				stack,
//			int						xOffset,
//			int						yOffset,
//			Operation<Void>			original
//	) {
//		if (!CoreFeature.isGuiBatching()) {
//			original.call(
//					instance,
//					guiGraphics,
//					font,
//					stack,
//					xOffset,
//					yOffset
//			);
//			return;
//		}
//
//		GuiBatchingController.INSTANCE.recordDecorator(
//				guiGraphics,
//				instance,
//				font,
//				stack,
//				xOffset,
//				yOffset
//		);
//	}

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
		if (		!AcceleratedItemRenderingFeature.isEnabled						()
				||	!AcceleratedItemRenderingFeature.shouldUseAcceleratedPipeline	()
				||	!AcceleratedItemRenderingFeature.shouldAccelerateInGui			()
				||	!CoreFeature					.isLoaded						()
		) {
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
			return;
		}

		var useGuiBatching	= CoreFeature.isGuiBatching();
		var useFlatLight	= useGuiBatching && !bakedModel.usesBlockLight();

		if (useFlatLight) {
			CoreFeature.forceSetDefaultLayer				(1);
			CoreFeature.forceSetDefaultLayerBeforeFunction	(Lighting::setupForFlatItems);
			CoreFeature.forceSetDefaultLayerAfterFunction	(Lighting::setupFor3DItems);
		}

		CoreFeature.setRenderingGui();

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

		CoreFeature.resetRenderingGui();

		if (useFlatLight) {
			CoreFeature.resetDefaultLayer				();
			CoreFeature.resetDefaultLayerBeforeFunction	();
			CoreFeature.resetDefaultLayerAfterFunction	();
		}

		if (!useGuiBatching) {
			flushItemBatching();
		}
	}

	@Unique
	@Override
	public void flushItemBatching() {
		CoreStates						.recordBuffers	();
		CoreBuffers.ENTITY				.prepareBuffers	();
		CoreBuffers.BLOCK				.prepareBuffers	();
		CoreBuffers.POS					.prepareBuffers	();
		CoreBuffers.POS_COLOR			.prepareBuffers	();
		CoreBuffers.POS_TEX				.prepareBuffers	();
		CoreBuffers.POS_TEX_COLOR		.prepareBuffers	();
		CoreBuffers.POS_COLOR_TEX_LIGHT	.prepareBuffers	();
		CoreStates						.restoreBuffers	();

		CoreBuffers.ENTITY				.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.BLOCK				.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS					.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS_COLOR			.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS_TEX				.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS_TEX_COLOR		.drawBuffers	(LayerDrawType.ALL);
		CoreBuffers.POS_COLOR_TEX_LIGHT	.drawBuffers	(LayerDrawType.ALL);

		CoreBuffers.ENTITY				.clearBuffers	();
		CoreBuffers.BLOCK				.clearBuffers	();
		CoreBuffers.POS					.clearBuffers	();
		CoreBuffers.POS_COLOR			.clearBuffers	();
		CoreBuffers.POS_TEX				.clearBuffers	();
		CoreBuffers.POS_TEX_COLOR		.clearBuffers	();
		CoreBuffers.POS_COLOR_TEX_LIGHT	.clearBuffers	();
	}
}

package com.github.argon4w.acceleratedrendering.features.items.mixins.models;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.items.BakedModelExtension;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.SeparateTransformsModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@ExtensionMethod(BakedModelExtension			.class)
@Mixin			(SeparateTransformsModel.Baked	.class)
public class BakedSeparateTransformsModelMixin implements IAcceleratedBakedModel {

	@Shadow @Final private BakedModel baseModel;

	@Override
	public void renderItemFast(
			ItemStack					itemStack,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	extension,
			int							light,
			int							overlay
	) {
		baseModel
				.getAccelerated()
				.renderItemFast(
						itemStack,
						random,
						pose,
						extension,
						light,
						overlay
				);
	}

	@Override
	public void renderBlockFast(
			BlockState					blockState,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	extension,
			int							light,
			int							overlay,
			int							color,
			ModelData					data,
			RenderType					renderType
	) {
		baseModel
				.getAccelerated	()
				.renderBlockFast(
						blockState,
						random,
						pose,
						extension,
						light,
						overlay,
						color,
						data,
						renderType
				);
	}

	@Override
	public int getCustomColor(int layer, int color) {
		return baseModel
				.getAccelerated()
				.getCustomColor(layer, color);
	}

	@Override
	public boolean isAccelerated() {
		return baseModel
				.getAccelerated	()
				.isAccelerated	();
	}

	@Override
	public boolean isAcceleratedInHand() {
		return baseModel
				.getAccelerated		()
				.isAcceleratedInHand();
	}

	@Override
	public boolean isAcceleratedInGui() {
		return baseModel
				.getAccelerated		()
				.isAcceleratedInGui	();
	}
}

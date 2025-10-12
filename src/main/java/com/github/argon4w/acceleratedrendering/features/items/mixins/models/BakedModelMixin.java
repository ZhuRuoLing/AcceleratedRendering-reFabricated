package com.github.argon4w.acceleratedrendering.features.items.mixins.models;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BakedModel.class)
public interface BakedModelMixin extends IAcceleratedBakedModel {

	@Unique
	@Override
	default void renderItemFast(
			ItemStack					itemStack,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	vertexConsumer,
			int							light,
			int							overlay
	) {
		throw new UnsupportedOperationException("Unsupported Operation.");
	}

	@Unique
	@Override
	default void renderBlockFast(
			BlockState					state,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	extension,
			int							light,
			int							overlay,
			int							color,
			ModelData					data,
			RenderType					renderType
	) {
		throw new UnsupportedOperationException("Unsupported Operation.");
	}

	@Unique
	@Override
	default boolean isAccelerated() {
		return false;
	}

	@Unique
	@Override
	default boolean isAcceleratedInHand() {
		return false;
	}

	@Unique
	@Override
	default boolean isAcceleratedInGui() {
		return false;
	}

	@Unique
	@Override
	default int getCustomColor(int layer, int color) {
		return color;
	}
}

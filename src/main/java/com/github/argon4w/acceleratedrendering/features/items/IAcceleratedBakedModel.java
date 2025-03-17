package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IAcceleratedBakedModel {

	void	renderItemFast		(ItemStack	itemStack,	RandomSource	random, PoseStack.Pose pose, IAcceleratedVertexConsumer extension, int light, int overlay);
	void	renderBlockFast		(BlockState	blockState, RandomSource	random, PoseStack.Pose pose, IAcceleratedVertexConsumer extension, int light, int overlay, int color);
	int		getCustomColor 		(int		layer,		int				color);
	boolean	isAccelerated		();
	boolean	isAcceleratedInHand	();
	boolean isAcceleratedInGui	();
}

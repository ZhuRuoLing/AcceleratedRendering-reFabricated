package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public interface IAcceleratedBakedModel {

	void	renderItemFast		(ItemStack	itemStack,	RandomSource	random, PoseStack.Pose pose, IAcceleratedVertexConsumer extension, int light, int overlay);
	void	renderBlockFast		(BlockState	blockState, RandomSource	random, PoseStack.Pose pose, IAcceleratedVertexConsumer extension, int light, int overlay, int color, ModelData data, RenderType renderType);
	int		getCustomColor 		(int		layer,		int				color);
	boolean	isAccelerated		();
	boolean	isAcceleratedInHand	();
	boolean isAcceleratedInGui	();
}

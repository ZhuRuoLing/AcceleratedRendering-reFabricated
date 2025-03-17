package com.github.argon4w.acceleratedrendering.features.items.colors;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.state.BlockState;

public class BlockLayerColors implements ILayerColors {

	private final BlockState blockState;
	private final BlockColor blockColor;

	public BlockLayerColors(BlockState blockState) {
		this.blockState = blockState;
		this.blockColor = ColorHelper.getBlockColorOrDefault(this.blockState.getBlock());
	}

	@Override
	public int getColor(int layer) {
		return layer == -1 ? -1 : blockColor.getColor(
				blockState,
				null,
				null,
				layer
		);
	}
}

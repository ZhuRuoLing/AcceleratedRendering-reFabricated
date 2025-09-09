package com.github.argon4w.acceleratedrendering.features.items.colors;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class EmptyBlockColor implements BlockColor {

	public static final BlockColor INSTANCE = new EmptyBlockColor();

	@Override
	public int getColor(
			BlockState			state,
			BlockAndTintGetter	level,
			BlockPos			pos,
			int					tintIndex
	) {
		return -1;
	}
}

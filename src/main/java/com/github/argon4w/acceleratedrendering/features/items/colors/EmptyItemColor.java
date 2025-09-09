package com.github.argon4w.acceleratedrendering.features.items.colors;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class EmptyItemColor implements ItemColor {

	public static final ItemColor INSTANCE = new EmptyItemColor();

	@Override
	public int getColor(ItemStack pStack, int pTintIndex) {
		return -1;
	}
}

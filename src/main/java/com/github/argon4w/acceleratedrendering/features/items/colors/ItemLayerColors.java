package com.github.argon4w.acceleratedrendering.features.items.colors;

import com.github.argon4w.acceleratedrendering.features.items.mixins.accessors.ItemColorsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class ItemLayerColors implements ILayerColors {

	private final ItemStack itemStack;
	private final ItemColor itemColor;

	public ItemLayerColors(ItemStack itemStack) {
		this.itemStack = itemStack;
		this.itemColor = ((ItemColorsAccessor) Minecraft.getInstance().getItemColors()).getItemColors().getOrDefault(this.itemStack.getItem(), EmptyItemColor.INSTANCE);
	}

	@Override
	public int getColor(int layer) {
		return itemColor.getColor(itemStack, layer);
	}
}

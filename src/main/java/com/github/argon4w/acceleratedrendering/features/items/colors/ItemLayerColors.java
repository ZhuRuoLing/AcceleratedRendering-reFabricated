package com.github.argon4w.acceleratedrendering.features.items.colors;

import com.github.argon4w.acceleratedrendering.core.utils.FastColorUtils;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class ItemLayerColors implements ILayerColors {

	private final ItemStack itemStack;
	private final ItemColor itemColor;

	public ItemLayerColors(ItemStack itemStack) {
		this.itemStack = itemStack;
		this.itemColor = ColorHelper.getItemColorOrDefault(itemStack);
	}

	@Override
	public int getColor(int layer) {
		return layer == -1 ? -1 : FastColorUtils.opaque(itemColor.getColor(itemStack, layer));
	}
}

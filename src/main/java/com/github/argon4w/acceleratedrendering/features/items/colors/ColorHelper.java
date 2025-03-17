package com.github.argon4w.acceleratedrendering.features.items.colors;

import com.github.argon4w.acceleratedrendering.features.items.mixins.MCItemColorsAccessor;
import com.github.argon4w.acceleratedrendering.features.items.mixins.accessors.BlockColorsAccessor;
import com.github.argon4w.acceleratedrendering.features.items.mixins.accessors.ItemColorsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ColorHelper {
    public static BlockColor getBlockColorOrDefault(Block block) {
        IdMapper<BlockColor> blockColors = ((BlockColorsAccessor) Minecraft.getInstance().getBlockColors()).getBlockColors();
        int id = BuiltInRegistries.BLOCK.getId(block);
        BlockColor blockColor = blockColors.byId(id);
        if (blockColor == null) return EmptyBlockColor.INSTANCE;
        return blockColor;
    }

    public static ItemColor getItemColorOrDefault(ItemStack itemStack) {
        MCItemColorsAccessor accessor = (MCItemColorsAccessor) Minecraft.getInstance();
        int id = BuiltInRegistries.ITEM.getId(itemStack.getItem());
        ItemColor color = ((ItemColorsAccessor) (accessor.getItemColors())).getItemColors().byId(id);
        if (color == null) return EmptyItemColor.INSTANCE;
        return color;
    }
}

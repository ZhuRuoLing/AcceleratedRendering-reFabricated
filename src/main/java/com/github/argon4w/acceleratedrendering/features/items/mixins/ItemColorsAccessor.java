package com.github.argon4w.acceleratedrendering.features.items.mixins;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.IdMapper;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ItemColors.class)
public interface ItemColorsAccessor {

    @Accessor("itemColors")
    IdMapper<ItemColor> getItemColors();
}

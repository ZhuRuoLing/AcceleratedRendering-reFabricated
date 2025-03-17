package com.github.argon4w.acceleratedrendering.core.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MCItemColorsAccessor {
    @Accessor
    ItemColors getItemColors();
}

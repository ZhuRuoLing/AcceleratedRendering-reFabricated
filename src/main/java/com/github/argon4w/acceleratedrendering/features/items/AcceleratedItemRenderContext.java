package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.features.items.mixins.MCItemColorsAccessor;
import com.github.argon4w.acceleratedrendering.features.items.mixins.ItemColorsAccessor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

@Getter
public class AcceleratedItemRenderContext {

	private final ItemStack		itemStack;
	private final ItemColor		itemColor;
	private final BakedModel	bakedModel;
	private final RandomSource	random;

    public AcceleratedItemRenderContext(
        ItemStack		itemStack,
        BakedModel		bakedModel,
        RandomSource	random
    ) {
        this.itemStack	= itemStack;
        this.itemColor	= getItemColorOrDefault(this.itemStack);
        this.bakedModel	= bakedModel;
        this.random		= random;
    }

    private static ItemColor getItemColorOrDefault(ItemStack itemStack) {
        MCItemColorsAccessor    accessor    = (MCItemColorsAccessor)    Minecraft.getInstance();
        int                     id          =                           BuiltInRegistries.ITEM.getId(itemStack.getItem());
        ItemColor               color       = ((ItemColorsAccessor)     (accessor.getItemColors())).getItemColors().byId(id);
        if (color == null) {
            return EmptyItemColor.INSTANCE;
        }
        return color;
    }
}

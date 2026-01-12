package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.features.items.gui.GuiBatchingController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.ItemDecoratorHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ItemDecoratorHandler.class)
public class ItemDecorationHandlerMixin {

	@Shadow(remap = false) @Final
	private List<IItemDecorator> itemDecorators;

	@WrapMethod(
			method	= "render",
			remap	= false
	)
	public void wrapRenderCustomDecoration(
			GuiGraphics		guiGraphics,
			Font			font,
			ItemStack		stack,
			int				xOffset,
			int				yOffset,
			Operation<Void> original
	) {
		var useOverlay = !itemDecorators.isEmpty();

		if (useOverlay) {
			GuiBatchingController.INSTANCE.useOverlayTarget(guiGraphics);
		}

		original.call(
				guiGraphics,
				font,
				stack,
				xOffset,
				yOffset
		);

		if (useOverlay) {
			GuiBatchingController.INSTANCE.resetOverlayTarget();
		}
	}
}

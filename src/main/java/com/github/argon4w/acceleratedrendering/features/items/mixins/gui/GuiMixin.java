package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.github.argon4w.acceleratedrendering.features.items.contexts.DecorationRenderContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Gui.class)
public class GuiMixin {

	@Shadow @Final private	Minecraft						minecraft;

	@Unique private final	List<DecorationRenderContext>	decorations = new ObjectArrayList<>();

	@Inject(
			method	= "renderItemHotbar",
			at		= @At("HEAD")
	)
	public void startBatching(
			GuiGraphics		guiGraphics,
			DeltaTracker	deltaTracker,
			CallbackInfo	ci
	) {
		if (		AcceleratedItemRenderingFeature.isEnabled				()
				&&	AcceleratedItemRenderingFeature.shouldAccelerateInGui	()
				&&	AcceleratedItemRenderingFeature.shouldUseGuiItemBatching()
		) {
			CoreFeature.setGuiBatching();
		}
	}

	@Inject(
			method	= "renderItemHotbar",
			at		= @At("TAIL")
	)
	public void flushBatching(
			GuiGraphics		guiGraphics,
			DeltaTracker	deltaTracker,
			CallbackInfo	ci
	) {
		CoreFeature								.resetGuiBatching	();
		((IAcceleratedGuiGraphics) guiGraphics)	.flushItemBatching	();

		for (var context : decorations) {
			guiGraphics.renderItemDecorations(
					minecraft.font,
					context.itemStack	(),
					context.slotX		(),
					context.slotY		()
			);
		}

		decorations.clear();
	}

	@Inject(
			cancellable	= true,
			method		= "renderSlot",
			at			= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
					shift	= At.Shift.BEFORE
			)
	)
	public void recordSlotItems(
			GuiGraphics		guiGraphics,
			int				x,
			int				y,
			DeltaTracker	deltaTracker,
			Player			player,
			ItemStack		stack,
			int				seed,
			CallbackInfo	ci
	) {
		if (CoreFeature.isGuiBatching()) {
			ci			.cancel	();
			decorations	.add	(new DecorationRenderContext(
					stack,
					null,
					x,
					y
			));
		}
	}
}

package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.github.argon4w.acceleratedrendering.features.items.contexts.DecorationRenderContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends ScreenMixin {

	@Shadow public abstract int getSlotColor(int index);

	@Unique private final List<DecorationRenderContext> decorations	= new ObjectArrayList<>();
	@Unique private final List<Slot>					highlights	= new ObjectArrayList<>();

	@Inject(
			method	= "render",
			at		= @At("HEAD")
	)
	public void startBatching(
			GuiGraphics		guiGraphics,
			int				mouseX,
			int				mouseY,
			float			partialTick,
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
			method	= "render",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V",
					shift	= At.Shift.BEFORE
			)
	)
	public void flushBatching(
			GuiGraphics		guiGraphics,
			int				mouseX,
			int				mouseY,
			float			partialTick,
			CallbackInfo	ci
	) {
		CoreFeature								.resetGuiBatching	();
		((IAcceleratedGuiGraphics) guiGraphics)	.flushItemBatching	();

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

		for (var context : decorations) {
			guiGraphics.renderItemDecorations(
					font,
					context.itemStack	(),
					context.slotX		(),
					context.slotY		(),
					context.countString	()
			);
		}

		guiGraphics.pose().popPose();

		for (var slot : highlights) {
			var slotX = slot.x;
			var slotY = slot.y;
			var color = getSlotColor(slot.index);

			guiGraphics.fillGradient(
					RenderType.guiOverlay(),
					slotX,
					slotY,
					slotX + 16,
					slotY + 16,
					color,
					color,
					0
			);
		}

		highlights	.clear();
		decorations	.clear();
	}

	@Inject(
			cancellable	= true,
			method		= "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V",
			at			= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;IIII)V",
					shift	= At.Shift.BEFORE
			)
	)
	public void recordSlotHighlights(
			GuiGraphics		guiGraphics,
			Slot			slot,
			int				mouseX,
			int				mouseY,
			float			partialTick,
			CallbackInfo	ci
	) {
		if (CoreFeature.isGuiBatching()) {
			ci			.cancel	();
			highlights	.add	(slot);
		}
	}

	@Inject(
			cancellable	= true,
			method		= "renderSlotContents",
			at			= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
					shift	= At.Shift.BEFORE
			)
	)
	public void recordSlotItems(
			GuiGraphics		guiGraphics,
			ItemStack		itemstack,
			Slot			slot,
			String			countString,
			CallbackInfo	ci
	) {
		if (CoreFeature.isGuiBatching()) {
			ci			.cancel	();
			decorations	.add	(new DecorationRenderContext(
					itemstack,
					countString,
					slot.x,
					slot.y
			));
		}
	}
}

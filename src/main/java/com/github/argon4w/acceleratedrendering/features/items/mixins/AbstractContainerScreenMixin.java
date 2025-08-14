package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.DecorationRenderContext;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends ScreenMixin {

//	@Shadow public abstract int getSlotColor(int index);

	@Shadow @Nullable protected Slot hoveredSlot;
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
					target	= "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"
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

			guiGraphics.fillGradient(
					RenderType.guiOverlay(),
					slotX,
					slotY,
					slotX + 16,
					slotY + 16,
				-2130706433,
				-2130706433,
					0
			);
		}

		highlights	.clear();
		decorations	.clear();
	}

	@WrapOperation(
			method		= "render",
			at			= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V")
	)
    private void recordSlotHighlights(
		GuiGraphics guiGraphics,
		int x,
		int y,
		int blitOffset,
		Operation<Void> original
    ) {
		if (CoreFeature.isGuiBatching()) {
			highlights.add(this.hoveredSlot);
			return;
		}
		original.call(guiGraphics, x, y, blitOffset);
	}

	@WrapOperation(
			method		= "renderSlot",
			at			= @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
			)
	)
	public void recordSlotItems(
		GuiGraphics         instance,
		Font                font,
		ItemStack           stack,
		int                 x,
		int                 y,
		String              text,
		Operation<Void>     original
	) {
        if (CoreFeature.isGuiBatching()) {
            decorations.add(new DecorationRenderContext(
                stack,
                text,
                x,
                y
            ));
            return;
        }
        original.call(instance, font, stack, x, y, text);
	}
}

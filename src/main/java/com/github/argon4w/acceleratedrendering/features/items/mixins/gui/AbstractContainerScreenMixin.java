package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.GuiBatchingController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

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
		GuiBatchingController.startBatching();
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
		GuiBatchingController.flushBatching(guiGraphics);
	}

	@Inject(
			method	= "renderSlotHighlight",
			at		= @At("HEAD")
	)
    private static void startRenderHighlight(
        GuiGraphics guiGraphics, int x, int y, int blitOffset, CallbackInfo ci
    ) {
		if (CoreFeature.isGuiBatching()) {
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.bindWrite(false);
		}
	}

	@Inject(
			method	= "renderSlotHighlight",
			at		= @At("TAIL")
	)
    private static void stopRenderHighlight(
        GuiGraphics guiGraphics, int x, int y, int blitOffset, CallbackInfo ci
    ) {
		if (CoreFeature.isGuiBatching()) {
			Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
		}
	}

	@WrapOperation(
		method = "renderSlot",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
		)
	)
	void onRenderItemDecorations(GuiGraphics instance, Font font, ItemStack stack, int x, int y, String text, Operation<Void> original){
		if (CoreFeature.isGuiBatching()) {
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.bindWrite(false);
		}
		original.call(instance, font, stack, x, y, text);
		if (CoreFeature.isGuiBatching()) {
			AcceleratedItemRenderingFeature.GUI_OVERLAY_TARGET.bindWrite(false);
		}
	}
}

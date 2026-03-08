package com.github.argon4w.acceleratedrendering.features.items.mixins.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.features.items.gui.GuiBatchingController;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

	@SuppressWarnings	("rawtypes")
	@WrapOperation		(
			method	= "renderBackground",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V"
			)
	)
	public void immediateDrawTransparentBackground(
			AbstractContainerScreen	instance,
			GuiGraphics				guiGraphics,
			Operation<Void>			original
	) {
		CoreFeature.forceBypassGuiItemBatching();

		original.call(instance, guiGraphics);

		CoreFeature.resetBypassGuiBatching();
	}

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
		GuiBatchingController.INSTANCE.startBatching(guiGraphics);
	}

	@Inject(
			method	= "render",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V",
					shift	= At.Shift.AFTER
			)
	)
	public void flushBatching(
			GuiGraphics		guiGraphics,
			int				mouseX,
			int				mouseY,
			float			partialTick,
			CallbackInfo	ci
	) {
		GuiBatchingController.INSTANCE.flushBatching(guiGraphics);
	}

    @WrapMethod(method = "renderSlotHighlight")
    private static void startRenderHighlight(
        GuiGraphics guiGraphics, int x, int y, int blitOffset, Operation<Void> original
	) {
		if (		!	CoreFeature.isLoaded				()
				||	!	CoreFeature.isGuiBatching			()
				||		CoreFeature.shouldByPassGuiBatching	()
		) {
			original.call(
					guiGraphics,
                	x,
					y,
					blitOffset
			);
			return;
		}

		var last = guiGraphics.pose().last();

		GuiBatchingController.INSTANCE.submitHighlight(
				last.pose	(),
				last.normal	(),
				x,
				y,
				blitOffset,
                -2130706433
		);
	}
}

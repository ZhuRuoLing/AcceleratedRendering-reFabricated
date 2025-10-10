package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.compat.iris.programs.IrisPrograms;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AcceleratedRenderingModEntry.class)
public class AcceleratedRenderingModEntryMixin {

	@Inject(method = "conditionalInitialize", at = @At("TAIL"), remap = false)
	public void registerIrisEvents(
		IEventBus modEventBus,
		CallbackInfo ci
	) {
		modEventBus.register(IrisPrograms.class);
	}
}

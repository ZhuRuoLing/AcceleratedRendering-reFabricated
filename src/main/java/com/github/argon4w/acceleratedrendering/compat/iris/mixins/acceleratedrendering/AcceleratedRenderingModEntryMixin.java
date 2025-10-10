package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.compat.iris.programs.IrisPrograms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AcceleratedRenderingModEntry.class)
public class AcceleratedRenderingModEntryMixin {

	@Inject(
			method	= "<init>",
			at		= @At("TAIL"),
			remap	= false
	)
	public void registerIrisEvents(FMLJavaModLoadingContext context, CallbackInfo	ci) {
		context
				.getModEventBus	()
				.register		(IrisPrograms.class);
	}
}

package com.github.argon4w.acceleratedrendering.core.mixins.compatibility;

import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.text.AcceleratedTextRenderingFeature;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

	@Inject(
			method	= "render",
			at		= @At("HEAD")
	)
	public void disableParticleAcceleration(
		LightTexture lightTexture,
		Camera camera,
		float partialTick,
		CallbackInfo ci
	) {
		AcceleratedEntityRenderingFeature	.useVanillaPipeline();
		AcceleratedItemRenderingFeature		.useVanillaPipeline();
		AcceleratedTextRenderingFeature		.useVanillaPipeline();
	}

	@Inject(
		method	= "render",
		at		= @At("RETURN")
	)
	public void resetParticleAcceleration(
		LightTexture lightTexture,
		Camera camera,
		float partialTick,
		CallbackInfo ci
	) {
		AcceleratedEntityRenderingFeature	.resetPipeline();
		AcceleratedItemRenderingFeature		.resetPipeline();
		AcceleratedTextRenderingFeature		.resetPipeline();
	}
}

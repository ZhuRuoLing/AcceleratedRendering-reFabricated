package com.github.argon4w.acceleratedrendering.core.mixins;

import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.text.AcceleratedTextRenderingFeature;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

	@Inject(
			method	= "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
			at		= @At("HEAD")
	)
	public void disableParticleAcceleration(
			LightTexture					lightTexture,
			Camera							camera,
			float							partialTick,
			Frustum							frustum,
			Predicate<ParticleRenderType>	renderTypePredicate,
			CallbackInfo					ci
	) {
		AcceleratedEntityRenderingFeature	.useVanillaPipeline();
		AcceleratedItemRenderingFeature		.useVanillaPipeline();
		AcceleratedTextRenderingFeature		.useVanillaPipeline();
	}

	@Inject(
			method	= "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V",
			at		= @At("RETURN")
	)
	public void resetParticleAcceleration(
			LightTexture					lightTexture,
			Camera							camera,
			float							partialTick,
			Frustum							frustum,
			Predicate<ParticleRenderType>	renderTypePredicate,
			CallbackInfo					ci
	) {
		AcceleratedEntityRenderingFeature	.resetPipeline();
		AcceleratedItemRenderingFeature		.resetPipeline();
		AcceleratedTextRenderingFeature		.resetPipeline();
	}
}

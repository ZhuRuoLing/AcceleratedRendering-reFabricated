package com.github.argon4w.acceleratedrendering.core.mixins.compatibility;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(RenderType.class)
public class RenderTypeMixin {

	@Unique private static final Map<Pair<ResourceLocation, Integer>, RenderType> ENERGY_SWIRL = new ConcurrentHashMap<>();

	@WrapMethod(method = "energySwirl")
	private static RenderType cacheEnergySwirl(
			ResourceLocation		location,
			float					u,
			float					v,
			Operation<RenderType>	original
	) {
		return ENERGY_SWIRL.computeIfAbsent(Pair.of(location, CoreFeature.packDynamicUV(u, v)), pair -> original.call(
				location,
				CoreFeature.unpackDynamicU(pair.getSecond()),
				CoreFeature.unpackDynamicV(pair.getSecond())
		));
	}

	@Inject(
			method		= "end",
			at			= @At(
					value	= "INVOKE",
					target	= "Lcom/mojang/blaze3d/vertex/BufferBuilder;end()Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;",
					shift	= At.Shift.BEFORE
			),
			cancellable	= true
	)
	public void fastEnd(
			BufferBuilder bufferBuilder,
			VertexSorting quadSorting,
			CallbackInfo ci
	) {
		if (bufferBuilder.vertices == 0) {
			bufferBuilder	.reset	();
			ci				.cancel	();
		}
	}
}

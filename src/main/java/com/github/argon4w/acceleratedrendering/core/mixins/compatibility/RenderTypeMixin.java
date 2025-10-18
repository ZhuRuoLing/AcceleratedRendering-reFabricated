package com.github.argon4w.acceleratedrendering.core.mixins.compatibility;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(RenderType.class)
public class RenderTypeMixin {

	@Unique private static final Map<Pair<ResourceLocation, Integer>, RenderType> ENERGY_SWIRL	= new ConcurrentHashMap<>();
	@Unique private static final Map<Pair<ResourceLocation, Integer>, RenderType> BREEZE_WIND	= new ConcurrentHashMap<>();

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

	@WrapMethod(method = "breezeWind")
	private static RenderType cacheBreezeWind(
			ResourceLocation		location,
			float					u,
			float					v,
			Operation<RenderType>	original
	) {
		return BREEZE_WIND.computeIfAbsent(Pair.of(location, CoreFeature.packDynamicUV(u, v)), pair -> original.call(
				location,
				CoreFeature.unpackDynamicU(pair.getSecond()),
				CoreFeature.unpackDynamicV(pair.getSecond())
		));
	}
}

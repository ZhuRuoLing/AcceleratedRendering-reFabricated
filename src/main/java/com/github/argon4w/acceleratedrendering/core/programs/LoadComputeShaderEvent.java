package com.github.argon4w.acceleratedrendering.core.programs;

import com.github.argon4w.acceleratedrendering.core.backends.programs.BarrierFlags;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Map;

public class LoadComputeShaderEvent extends Event implements IModBusEvent {

	private final ImmutableMap.Builder<ResourceLocation, ComputeShaderDefinition> shaderLocations;

	public LoadComputeShaderEvent() {
		this.shaderLocations = ImmutableMap.builder();
	}

	public void loadComputeShader(
			ResourceLocation	key,
			ResourceLocation	location,
			BarrierFlags...		barrierFlags
	) {
		shaderLocations.put(key, new ComputeShaderDefinition(location, BarrierFlags.getFlags(barrierFlags)));
	}

	public Map<ResourceLocation, ComputeShaderDefinition> build() {
		return shaderLocations.build();
	}
}

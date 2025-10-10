package com.github.argon4w.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.github.argon4w.acceleratedrendering.features.culling.OrientationCullingPrograms;
import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

public class AcceleratedRenderingModEntry implements ClientModInitializer {

	public static final String MOD_ID = "acceleratedrendering";
	public static final Logger LOGGER = LogUtils.getLogger();

	@Getter
	private static ModContainer container;

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	@Override
	public void onInitializeClient() {
		ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, FeatureConfig.SPEC);
		container = ModLoader.get().createModContainer(MOD_ID);
		IEventBus eventBus = container.getModEventBus();
		eventBus.register(ComputeShaderPrograms.class);
		eventBus.register(OrientationCullingPrograms.class);
		conditionalInitialize(container.getModEventBus());
	}

	public void conditionalInitialize(IEventBus modEventBus) {
		//intentionally empty
	}
}

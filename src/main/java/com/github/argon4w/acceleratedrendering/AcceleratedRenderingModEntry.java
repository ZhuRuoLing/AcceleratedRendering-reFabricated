package com.github.argon4w.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.programs.IrisPrograms;
import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.github.argon4w.acceleratedrendering.features.culling.OrientationCullingPrograms;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.config.ModConfig;

public class AcceleratedRenderingModEntry implements ClientModInitializer {

    public static final String MODID = "acceleratedrendering";
    private ModContainer container;

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @Override
    public void onInitializeClient() {
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, FeatureConfig.SPEC);
        this.container = ModLoader.createModContainer(MODID);
        IEventBus eventBus = container.getModEventBus();
        eventBus.register(ComputeShaderPrograms.class);
        eventBus.register(OrientationCullingPrograms.class);
        conditionalInitialize(container.getModEventBus());
    }

    public void conditionalInitialize(IEventBus modEventBus) {
        //intentionally empty
    }
}

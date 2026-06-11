package com.github.argon4w.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderPrograms;
import com.github.argon4w.acceleratedrendering.core.utils.AvailabilityUtils;
import com.github.argon4w.acceleratedrendering.features.culling.OrientationCullingPrograms;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.screenhandler.client.ClientNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.config.ModConfig;

public class AcceleratedRenderingModEntry implements ClientModInitializer {

    public static final String MOD_ID = "acceleratedrendering";
    @Getter
    private static ModContainer container;

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        NeoForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, FeatureConfig.SPEC);
        container = ModLoader.createModContainer(MOD_ID);
        IEventBus eventBus = container.getModEventBus();
        eventBus.register(ComputeShaderPrograms.class);
        eventBus.register(OrientationCullingPrograms.class);
        conditionalInitialize(container.getModEventBus());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!AvailabilityUtils.isAvailable()) {
                client.player.displayClientMessage(
                    Component.translatable("acceleratedrendering.component.not_available")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
                );
            }
        });
    }

    public void conditionalInitialize(IEventBus modEventBus) {
        //intentionally empty
    }
}

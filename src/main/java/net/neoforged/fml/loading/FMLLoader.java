package net.neoforged.fml.loading;

import net.fabricmc.loader.api.FabricLoader;

public class FMLLoader {

    public static final FabricLoader DELEGATE = FabricLoader.getInstance();

    public static boolean isProduction() {
        return !DELEGATE.isDevelopmentEnvironment();
    }
}

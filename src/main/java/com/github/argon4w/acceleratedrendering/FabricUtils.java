package com.github.argon4w.acceleratedrendering;

import net.fabricmc.loader.api.FabricLoader;

public class FabricUtils {
    public static boolean modExists(String modid){
        return FabricLoader.getInstance().getModContainer(modid).isPresent();
    }
}

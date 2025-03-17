package net.neoforged.neoforgespi.language;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ModInfo implements IModInfo {

    private final ModMetadata metadata;

    public ModInfo(String modid) {
        this.metadata = FabricLoader.getInstance().getModContainer(modid).orElseThrow().getMetadata();
    }

    @Override
    public String getDisplayName() {
        return metadata.getName();
    }
}

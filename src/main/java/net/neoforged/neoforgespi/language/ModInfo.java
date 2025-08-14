package net.neoforged.neoforgespi.language;

import net.fabricmc.loader.api.metadata.ModMetadata;
import net.neoforged.fml.loading.FMLLoader;

public class ModInfo implements IModInfo {

    private final ModMetadata metadata;

    public ModInfo(String modid) {
        this.metadata = FMLLoader.DELEGATE.getModContainer(modid).orElseThrow().getMetadata();
    }

    @Override
    public String getDisplayName() {
        return metadata.getName();
    }
}

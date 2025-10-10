package com.github.argon4w.acceleratedrendering;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ARModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return null;
        //return screen -> new ConfigScreen(AcceleratedRenderingModEntry.getContainer(), screen);
    }
}

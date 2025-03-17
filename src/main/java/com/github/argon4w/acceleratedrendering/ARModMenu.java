package com.github.argon4w.acceleratedrendering;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.neoforged.neoforge.client.gui.ConfigScreen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ARModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new ConfigScreen(AcceleratedRenderingModEntry.getContainer(), screen);
    }
}

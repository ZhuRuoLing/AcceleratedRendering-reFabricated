package com.github.argon4w.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(value= AcceleratedRenderingModEntry.MOD_ID)
public class AcceleratedRenderingModEntry {

	public static final String MOD_ID = "acceleratedrendering";
	public static final Logger LOGGER = LogUtils.getLogger();

	public AcceleratedRenderingModEntry() {
		ModLoadingContext
				.get			()
				.registerConfig	(ModConfig.Type.CLIENT, FeatureConfig.SPEC);
	}
}

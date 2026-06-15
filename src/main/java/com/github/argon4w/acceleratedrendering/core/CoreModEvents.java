package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@SuppressWarnings	("removal")
@EventBusSubscriber	(
		modid	= AcceleratedRenderingModEntry	.MOD_ID,
		value	= Dist							.CLIENT,
		bus		= Bus							.MOD
)
public class CoreModEvents {

	@SubscribeEvent
	public static void onRegisterClientReloadListener(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new CoreReloads());
	}
}

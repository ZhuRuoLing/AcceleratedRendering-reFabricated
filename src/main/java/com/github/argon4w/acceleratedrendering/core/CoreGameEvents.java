package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.utils.AvailabilityUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@SuppressWarnings	("removal")
@EventBusSubscriber	(
		modid	= AcceleratedRenderingModEntry	.MOD_ID,
		value	= Dist							.CLIENT,
		bus		= Bus							.GAME
)
public class CoreGameEvents {

	@SubscribeEvent
	public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
		if (!AvailabilityUtils.isAvailable()) {
			event.getPlayer().displayClientMessage(Component.translatable("acceleratedrendering.component.not_available").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
		}
	}
}

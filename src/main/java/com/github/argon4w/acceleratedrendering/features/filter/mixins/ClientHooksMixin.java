package com.github.argon4w.acceleratedrendering.features.filter.mixins;

import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.filter.FilterFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.text.AcceleratedTextRenderingFeature;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientHooks.class)
public class ClientHooksMixin {

	@WrapOperation(
			method	= "dispatchRenderStage(Lnet/neoforged/neoforge/client/event/RenderLevelStageEvent$Stage;Lnet/minecraft/client/renderer/LevelRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;ILnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;)V",
			at		= @At(
					value	= "INVOKE",
					target	= "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"
			)
	)
	private static Event startRenderLevelStage(
			IEventBus					instance,
			Event						event,
			Operation<Event>			original,
			RenderLevelStageEvent.Stage	stage
	) {
		var pass =	!	FilterFeature.isEnabled			()
				||	!	FilterFeature.shouldFilterStage	()
				||		FilterFeature.testStage			(stage);

		if (!pass) {
			AcceleratedEntityRenderingFeature	.useVanillaPipeline();
			AcceleratedItemRenderingFeature		.useVanillaPipeline();
			AcceleratedTextRenderingFeature		.useVanillaPipeline();
		}

		var result = original.call(instance, event);

		if (!pass) {
			AcceleratedEntityRenderingFeature	.resetPipeline();
			AcceleratedItemRenderingFeature		.resetPipeline();
			AcceleratedTextRenderingFeature		.resetPipeline();
		}

		return result;
	}
}

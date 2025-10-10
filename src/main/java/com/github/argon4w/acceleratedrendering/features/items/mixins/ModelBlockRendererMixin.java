package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.utils.DirectionUtils;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedQuadsRenderer;
import com.github.argon4w.acceleratedrendering.features.items.BakedModelExtension;
import com.github.argon4w.acceleratedrendering.features.items.colors.FixedColors;
import com.github.argon4w.acceleratedrendering.features.items.contexts.AcceleratedQuadsRenderContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(value = {VertexConsumerExtension	.class, BakedModelExtension.class	})
@Mixin			(value = {ModelBlockRenderer		.class								}, priority = 999)
public class ModelBlockRendererMixin {

	@Inject(
			cancellable	= true,
			method		= "renderModel",
			at			= @At("HEAD"),
			remap		= false
	)
	public void renderModelFast(
			PoseStack.Pose		pose,
			VertexConsumer		consumer,
			BlockState			state,
			BakedModel			model,
			float				red,
			float				green,
			float				blue,
			int					packedLight,
			int					packedOverlay,
			RenderType			renderType,
			CallbackInfo		ci
	) {
		var extension1 = consumer	.getAccelerated();
		var extension2 = model		.getAccelerated();

		if (			!		AcceleratedItemRenderingFeature	.isEnabled						()
				||		!		AcceleratedItemRenderingFeature	.shouldUseAcceleratedPipeline	()
				||	(	!		CoreFeature						.isRenderingLevel				()

				&&		!	(	CoreFeature						.isRenderingHand				()
				&&			(	extension2						.isAcceleratedInHand			()
				||				AcceleratedItemRenderingFeature	.shouldAccelerateInHand			()))

				&&		!	(	CoreFeature						.isRenderingGui					()
				&&			(	extension2						.isAcceleratedInGui				()
				||				AcceleratedItemRenderingFeature	.shouldAccelerateInGui			())))
				||		!		extension1						.isAccelerated					()
		) {
			return;
		}

		if (extension2.isAccelerated()) {
			ci			.cancel			();
			extension2	.renderBlockFast(
					state,
					RandomSource.create(42),
					pose,
					extension1,
					packedLight,
					packedOverlay,
					FastColor.ARGB32.color(
							255,
							(int) (red		* 255.0f),
							(int) (green	* 255.0f),
							(int) (blue		* 255.0f)
					),
					renderType
			);
			return;
		}

		if (!AcceleratedItemRenderingFeature.shouldBakeMeshForQuad()) {
			return;
		}

		ci.cancel();

		var randomSource = RandomSource.create();

		for (var direction : DirectionUtils.FULL) {
			randomSource.setSeed	(42L);
			extension1	.doRender	(
					AcceleratedQuadsRenderer.INSTANCE,
					new AcceleratedQuadsRenderContext(
							model.getQuads(
									state,
									direction,
									randomSource
							),
							new FixedColors(FastColor.ARGB32.color(
									255,
									(int) (red		* 255.0f),
									(int) (green	* 255.0f),
									(int) (blue		* 255.0f)
							))
					),
					pose.pose	(),
					pose.normal	(),
					packedLight,
					packedOverlay,
					-1
			);
		}
	}
}

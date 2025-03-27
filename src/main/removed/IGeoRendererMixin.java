package com.github.argon4w.acceleratedrendering.features.touhoulittlemaid.mixins;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoBone;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@ExtensionMethod(VertexConsumerExtension.class)
@Mixin			(IGeoRenderer			.class)
public interface IGeoRendererMixin {

	@SuppressWarnings("unchecked")
	@WrapOperation(
			method		= "renderRecursively",
			at			= @At(
					value	= "INVOKE",
					target	= "Lcom/github/tartaricacid/touhoulittlemaid/compat/sodium/SodiumCompat;sodiumRenderCubesOfBone(Lcom/github/tartaricacid/touhoulittlemaid/geckolib3/geo/animated/AnimatedGeoBone;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)Z"
			)
	)
	default boolean renderBoneFast(
			AnimatedGeoBone		bone,
			PoseStack			poseStack,
			VertexConsumer		buffer,
			int					cubePackedLight,
			int					packedOverlay,
			float				red,
			float				green,
			float				blue,
			float				alpha,
			Operation<Boolean>	original
	) {
		var extension = buffer.getAccelerated();

		if (			AcceleratedEntityRenderingFeature	.isEnabled						()
				&&		AcceleratedEntityRenderingFeature	.shouldUseAcceleratedPipeline	()
				&&	(	CoreFeature							.isRenderingLevel				()
				||	(	CoreFeature							.isRenderingGui					()
				&&		AcceleratedEntityRenderingFeature	.shouldAccelerateInGui			()))
				&&		extension							.isAccelerated					()
		) {
			var pose = poseStack.last();

			extension.doRender(
					(IAcceleratedRenderer<Void>) bone	.geoBone(),
					null,
					pose								.pose	(),
					pose								.normal	(),
					cubePackedLight,
					packedOverlay,
					FastColor.ARGB32					.color	(
							(int) (alpha	* 255.0f),
							(int) (red		* 255.0f),
							(int) (green	* 255.0f),
							(int) (blue		* 255.0f)
					)
			);

			return true;
		}

		return original.call(
				bone,
				poseStack,
				buffer,
				cubePackedLight,
				packedOverlay,
				red,
				green,
				blue,
				alpha
		);
	}
}

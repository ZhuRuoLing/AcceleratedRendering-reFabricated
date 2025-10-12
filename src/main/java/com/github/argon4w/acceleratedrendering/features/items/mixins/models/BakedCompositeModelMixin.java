package com.github.argon4w.acceleratedrendering.features.items.mixins.models;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.items.BakedModelExtension;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Getter
@ExtensionMethod(BakedModelExtension	.class)
@Mixin			(CompositeModel.Baked	.class)
public class BakedCompositeModelMixin implements IAcceleratedBakedModel {

	@Shadow(remap = false) @Final private	ImmutableMap<String, BakedModel>	children;
	@Unique private							boolean								accelerated;
	@Unique private							boolean								acceleratedInHand;
	@Unique private							boolean								acceleratedInGui;

	@Inject(
			method	= "<init>",
			at		= @At("TAIL")
	)
	public void checkAccelerationSupport(
			boolean								isGui3d,
			boolean								isSideLit,
			boolean								isAmbientOcclusion,
			TextureAtlasSprite					particle,
			ItemTransforms						transforms,
			ItemOverrides						overrides,
			ImmutableMap<String, BakedModel>	children,
			ImmutableList<BakedModel>			itemPasses,
			CallbackInfo						ci
	) {
		accelerated			= true;
		acceleratedInHand	= true;
		acceleratedInGui	= true;

		for (BakedModel childModel : children.values()) {
			var extension = childModel.getAccelerated();

			accelerated			&= extension.isAccelerated		();
			acceleratedInHand	&= extension.isAcceleratedInHand();
			acceleratedInGui	&= extension.isAcceleratedInGui	();
		}
	}

	@Override
	public void renderItemFast(
			ItemStack					itemStack,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	extension,
			int							light,
			int							overlay
	) {
		for (BakedModel child : children.values()) {
			child
					.getAccelerated	()
					.renderItemFast(
							itemStack,
							random,
							pose,
							extension,
							light,
							overlay
					);
		}
	}

	@Override
	public void renderBlockFast(
			BlockState					blockState,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	extension,
			int							light,
			int							overlay,
			int							color,
			ModelData					data,
			RenderType					renderType
	) {
		if (blockState == null) {
			return;
		}

		for (BakedModel child : children.values()) {
			var renderTypeSet = child.getRenderTypes(
					blockState,
					random,
					data
			);

			if (renderTypeSet.contains(renderType)) {
				child
						.getAccelerated	()
						.renderBlockFast(
								blockState,
								random,
								pose,
								extension,
								light,
								overlay,
								getCustomColor(-1, color),
								data,
								renderType
						);
			}
		}
	}

	@Override
	public int getCustomColor(int layer, int color) {
		return color;
	}
}

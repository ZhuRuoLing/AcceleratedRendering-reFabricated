package com.github.argon4w.acceleratedrendering.features.items.mixins.models;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.items.BakedModelExtension;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Getter
@ExtensionMethod(BakedModelExtension.class)
@Mixin			(WeightedBakedModel	.class)
public class WeightedBakedModelMixin implements IAcceleratedBakedModel {

	@Unique private			boolean									accelerated;
	@Unique private			boolean									acceleratedInHand;
	@Unique private			boolean									acceleratedInGui;

	@Shadow @Final private	List<WeightedEntry.Wrapper<BakedModel>>	list;
	@Shadow @Final private	int										totalWeight;

	@Inject(
			method	= "<init>",
			at		= @At("TAIL")
	)
	public void checkAccelerationSupport(List<WeightedEntry.Wrapper<BakedModel>> list, CallbackInfo ci) {
		accelerated			= true;
		acceleratedInHand	= true;
		acceleratedInGui	= true;

		for (WeightedEntry.Wrapper<BakedModel> wrapper : list) {
			var extension = wrapper.data().getAccelerated();

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
		var model = WeightedRandom.getWeightedItem(list, Math.abs((int) random.nextLong()) % totalWeight);

		if (model.isPresent()) {
			model
					.get			()
					.data			()
					.getAccelerated	()
					.renderItemFast	(
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
			BlockState					state,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	extension,
			int							light,
			int							overlay,
			int							color
	) {
		var model = WeightedRandom.getWeightedItem(list, Math.abs((int) random.nextLong()) % totalWeight);

		if (model.isPresent()) {
			model
					.get			()
					.data			()
					.getAccelerated	()
					.renderBlockFast(
							state,
							random,
							pose,
							extension,
							light,
							overlay,
							getCustomColor(-1, color)
					);
		}
	}

	@Override
	public int getCustomColor(int layer, int color) {
		return color;
	}
}

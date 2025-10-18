package com.github.argon4w.acceleratedrendering.features.items.mixins.models;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.features.items.BakedModelExtension;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;
import java.util.List;
import java.util.function.Predicate;

@Getter
@ExtensionMethod(BakedModelExtension.class)
@Mixin			(MultiPartBakedModel.class)
public abstract class MultipartBakedModelMixin implements IAcceleratedBakedModel {


	@Unique private			boolean											accelerated;
	@Unique private			boolean											acceleratedInHand;
	@Unique private			boolean											acceleratedInGui;

	@Shadow @Final private	List<Pair<Predicate<BlockState>, BakedModel>>	selectors;

	@Shadow(remap = false) public abstract BitSet getSelectors(BlockState p_235050_);


	@Inject(
			method	= "<init>",
			at		= @At("TAIL")
	)
	public void checkAccelerationSupport(List<Pair<Predicate<BlockState>, BakedModel>> selectors, CallbackInfo ci) {
		accelerated			= true;
		acceleratedInHand	= true;
		acceleratedInGui	= true;

		for (Pair<Predicate<BlockState>, BakedModel> selector : selectors) {
			var extension = selector.getRight().getAccelerated();

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

		var bitset	= getSelectors		(blockState);
		var seed	= random.nextLong	();

		for (var j = 0; j < bitset.length(); j ++) {
			if (bitset.get(j)) {
				var selector		= selectors	.get			(j);
				var selected		= selector	.getRight		();
				var renderTypeSet	= selected	.getRenderTypes	(
						blockState,
						random,
						data
				);

				if (		renderType == null
						||	renderTypeSet.contains(renderType)
				) {
					selected
							.getAccelerated	()
							.renderBlockFast(
									blockState,
									RandomSource.create(seed),
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
	}

	@Override
	public int getCustomColor(int layer, int color) {
		return color;
	}
}

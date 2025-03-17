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
import java.util.Map;
import java.util.function.Predicate;

@Getter
@ExtensionMethod(BakedModelExtension.class)
@Mixin			(MultiPartBakedModel.class)
public abstract class MultipartBakedModelMixin implements IAcceleratedBakedModel {


	@Unique private			boolean											accelerated;
	@Unique private			boolean											acceleratedInHand;
	@Unique private			boolean											acceleratedInGui;

	@Shadow @Final private	List<Pair<Predicate<BlockState>, BakedModel>>	selectors;

	@Shadow @Final private Map<BlockState, BitSet> selectorCache;

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
			BlockState					state,
			RandomSource				random,
			PoseStack.Pose				pose,
			IAcceleratedVertexConsumer	extension,
			int							light,
			int							overlay,
			int							color
	) {
		var bitSet	= selectorCache.get(state);
		if (bitSet == null) {
			bitSet = new BitSet();
			for(int i = 0; i < this.selectors.size(); ++i) {
				Pair<Predicate<BlockState>, BakedModel> pair = this.selectors.get(i);
				if ((pair.getLeft()).test(state)) {
					bitSet.set(i);
				}
			}
			this.selectorCache.put(state, bitSet);
		}
		var seed	= random.nextLong	();

		for (var j = 0; j < bitSet.length(); j ++) {
			if (bitSet.get(j)) {
				selectors
						.get			(j)
						.getRight		()
						.getAccelerated	()
						.renderBlockFast(
								state,
								RandomSource.create(seed),
								pose,
								extension,
								light,
								overlay,
								getCustomColor(-1, color)
						);
			}
		}
	}

	@Override
	public int getCustomColor(int layer, int color) {
		return color;
	}
}

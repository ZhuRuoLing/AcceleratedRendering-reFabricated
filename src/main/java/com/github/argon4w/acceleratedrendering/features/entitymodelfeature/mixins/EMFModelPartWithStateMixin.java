package com.github.argon4w.acceleratedrendering.features.entitymodelfeature.mixins;

import com.github.argon4w.acceleratedrendering.features.modelparts.IAcceleratedModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPartWithState;

@Mixin(EMFModelPartWithState.class)
public class EMFModelPartWithStateMixin {

	@Inject(
			method	= "setFromState",
			at		= @At("RETURN"),
			remap	= false
	)
	public void clearCache(EMFModelPartWithState.EMFModelState newState, CallbackInfo ci) {
		((IAcceleratedModelPart) this).clearMeshCacheUnsafe();
	}
}

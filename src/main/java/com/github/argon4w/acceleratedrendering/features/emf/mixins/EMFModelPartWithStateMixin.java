package com.github.argon4w.acceleratedrendering.features.emf.mixins;

import com.github.argon4w.acceleratedrendering.features.emf.IEMFModelVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPartWithState;

@Pseudo
@Mixin(EMFModelPartWithState.class)
public class EMFModelPartWithStateMixin {

	@Shadow(remap = false) public int currentModelVariant;

	@Inject(
			method	= "resetState",
			at		= @At("RETURN"),
			remap	= false
	)
	public void resetEmfVariant(CallbackInfo ci) {
		((IEMFModelVariant) this).setCurrentVariant(currentModelVariant);
	}

	@Inject(
			method	= "setVariantStateTo",
			at		= @At("RETURN"),
			remap	= false
	)
	public void setEmfVariant(int newVariant, CallbackInfo ci) {
		((IEMFModelVariant) this).setCurrentVariant(newVariant);
	}
}

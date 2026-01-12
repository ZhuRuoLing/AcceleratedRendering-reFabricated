package com.github.argon4w.acceleratedrendering.features.emf.mixins;

import com.github.argon4w.acceleratedrendering.features.emf.IEMFModelVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPartWithState;

@Mixin(EMFModelPartWithState.class)
public class EMFModelPartWithStateMixin {

	@Shadow public int currentModelVariant;

	@Inject(
			method	= "resetState",
			at		= @At("RETURN")
	)
	public void resetEmfVariant(CallbackInfo ci) {
		((IEMFModelVariant) this).setCurrentVariant(currentModelVariant);
	}

	@Inject(
			method	= "setVariantStateTo",
			at		= @At("RETURN")
	)
	public void setEmfVariant(int newVariant, CallbackInfo ci) {
		((IEMFModelVariant) this).setCurrentVariant(newVariant);
	}
}

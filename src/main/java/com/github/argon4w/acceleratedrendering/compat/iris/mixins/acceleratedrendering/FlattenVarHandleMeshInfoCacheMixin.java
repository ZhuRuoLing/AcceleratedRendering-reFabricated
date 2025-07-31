package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.interfaces.IIrisMeshInfoCache;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.FlattenVarHandleMeshInfoCache;
import com.llamalad7.mixinextras.sugar.Local;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.invoke.VarHandle;

@Mixin(value = FlattenVarHandleMeshInfoCache.class, remap = false)
public class FlattenVarHandleMeshInfoCacheMixin implements IIrisMeshInfoCache {

	@Shadow @Final public	static			VarHandle	HANDLE;

	@Shadow private							int[]		cache;

	@Unique private			static final	int			IRIS_MESH_INFO_SIZE				= 8;
	@Unique private			static final	int			RENDERED_ENTITY_OFFSET			= 5;
	@Unique private			static final	int			RENDERED_BLOCK_ENTITY_OFFSET	= 6;
	@Unique private			static final	int			RENDERED_ITEM_OFFSET			= 7;

	@ModifyConstant(
			method		= {
					"<init>",
					"setup",
					"getSharing",
					"getShouldCull",
					"getColor",
					"getLight",
					"getOverlay"
			},
			constant	= @Constant(intValue = 5)
	)
	public int modifySize(int constant) {
		return IRIS_MESH_INFO_SIZE;
	}

	@Inject(
			method	= "setup",
			at		= @At(
					value	= "INVOKE",
					target	= "Ljava/lang/invoke/VarHandle;set([III)V",
					ordinal	= 4,
					shift	= At.Shift.AFTER
			)
	)
	public void addIrisData(
			int									color,
			int									light,
			int									overlay,
			int									sharing,
			int									shouldCull,
			CallbackInfo						ci,
			@Local(name = "infoIndex") int		infoIndex
	) {
		HANDLE.set(cache, infoIndex + RENDERED_ENTITY_OFFSET,		CapturedRenderingState.INSTANCE.getCurrentRenderedEntity		());
		HANDLE.set(cache, infoIndex + RENDERED_BLOCK_ENTITY_OFFSET,	CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity	());
		HANDLE.set(cache, infoIndex + RENDERED_ITEM_OFFSET,			CapturedRenderingState.INSTANCE.getCurrentRenderedItem			());
	}

	@Override
	public short getRenderedEntity(int i) {
		return (short) (int) HANDLE.get(cache, i * IRIS_MESH_INFO_SIZE + RENDERED_ENTITY_OFFSET);
	}

	@Override
	public short getRenderedBlockEntity(int i) {
		return (short) (int) HANDLE.get(cache, i * IRIS_MESH_INFO_SIZE + RENDERED_BLOCK_ENTITY_OFFSET);
	}

	@Override
	public short getRenderedItem(int i) {
		return (short) (int) HANDLE.get(cache, i * IRIS_MESH_INFO_SIZE + RENDERED_ITEM_OFFSET);
	}
}

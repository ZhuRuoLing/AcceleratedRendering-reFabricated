package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.interfaces.IIrisMeshInfoCache;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.UnsafeMemoryMeshInfoCache;
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
import sun.misc.Unsafe;

@Mixin(value = UnsafeMemoryMeshInfoCache.class, remap = false)
public class UnsafeMemoryMeshInfoCacheMixin implements IIrisMeshInfoCache {

	@Shadow @Final public	static			Unsafe		UNSAFE;

	@Shadow private							long		address;

	@Unique private			static final	long		IRIS_MESH_INFO_SIZE				= 8L * 4L;
	@Unique private			static final	long		RENDERED_ENTITY_OFFSET			= 5L * 4L;
	@Unique private			static final	long		RENDERED_BLOCK_ENTITY_OFFSET	= 6L * 4L;
	@Unique private			static final	long		RENDERED_ITEM_OFFSET			= 7L * 4L;

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
			constant	= @Constant(longValue = 20L)
	)
	public long modifySize(long constant) {
		return IRIS_MESH_INFO_SIZE;
	}

	@Inject(
			method	= "setup",
			at		= @At(
					value = "INVOKE",
					target = "Lsun/misc/Unsafe;putInt(JI)V",
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
			@Local(name = "infoAddress") long	infoAddress
	) {
		UNSAFE.putInt(infoAddress + RENDERED_ENTITY_OFFSET,			CapturedRenderingState.INSTANCE.getCurrentRenderedEntity		());
		UNSAFE.putInt(infoAddress + RENDERED_BLOCK_ENTITY_OFFSET,	CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity	());
		UNSAFE.putInt(infoAddress + RENDERED_ITEM_OFFSET,			CapturedRenderingState.INSTANCE.getCurrentRenderedItem			());
	}

	@Override
	public short getRenderedEntity(int i) {
		return (short) UNSAFE.getInt(address + i * IRIS_MESH_INFO_SIZE + RENDERED_ENTITY_OFFSET);
	}

	@Override
	public short getRenderedBlockEntity(int i) {
		return (short) UNSAFE.getInt(address + i * IRIS_MESH_INFO_SIZE + RENDERED_BLOCK_ENTITY_OFFSET);
	}

	@Override
	public short getRenderedItem(int i) {
		return (short) UNSAFE.getInt(address + i * IRIS_MESH_INFO_SIZE + RENDERED_ITEM_OFFSET);
	}
}

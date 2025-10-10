package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.ILayerStorage;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.storage.LayerStorageType;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.IMeshInfoCache;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.MeshInfoCacheType;
import com.google.common.util.concurrent.Runnables;

import java.util.ArrayDeque;

public class CoreFeature {

	private static final	ArrayDeque<FeatureStatus>	FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK	= new ArrayDeque<>();
	private static final	ArrayDeque<FeatureStatus>	CACHE_IDENTICAL_POSE_CONTROLLER_STACK			= new ArrayDeque<>();
	private static final	ArrayDeque<Integer>			DEFAULT_LAYER_CONTROLLER_STACK					= new ArrayDeque<>();
	private static final	ArrayDeque<Runnable>		DEFAULT_LAYER_BEFORE_FUNCTION_CONTROLLER_STACK	= new ArrayDeque<>();
	private static final	ArrayDeque<Runnable>		DEFAULT_LAYER_AFTER_FUNCTION_CONTROLLER_STACK	= new ArrayDeque<>();
	private static 			boolean						RENDERING_LEVEL									= false;
	private static			boolean						RENDERING_HAND									= false;
	private static			boolean						RENDERING_GUI									= false;
	private static			boolean						GUI_BATCHING									= false;

	public static boolean isDebugContextEnabled() {
		return FeatureConfig.CONFIG.coreDebugContextEnabled.get() == FeatureStatus.ENABLED;
	}

	public static int getPooledRingBufferSize() {
		return FeatureConfig.CONFIG.corePooledRingBufferSize.get();
	}

	public static int getPooledBatchingSize() {
		return FeatureConfig.CONFIG.corePooledBatchingSize.get();
	}

	public static int getCachedImageSize() {
		return FeatureConfig.CONFIG.coreCachedImageSize.get();
	}

	public static boolean shouldForceAccelerateTranslucent() {
		return getForceTranslucentAccelerationSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldCacheIdenticalPose() {
		return getCacheIdenticalPoseSetting() == FeatureStatus.ENABLED;
	}

	public static MeshInfoCacheType getMeshInfoCacheType() {
		return FeatureConfig.CONFIG.coreMeshInfoCacheType.get();
	}

	public static LayerStorageType getLayerStorageType() {
		return FeatureConfig.CONFIG.coreLayerStorageType.get();
	}

	public static boolean shouldUploadMeshImmediately() {
		return FeatureConfig.CONFIG.coreUploadMeshImmediately.get() == FeatureStatus.ENABLED;
	}

	public static IMeshInfoCache createMeshInfoCache() {
		return MeshInfoCacheType.create(getMeshInfoCacheType());
	}

	public static ILayerStorage createLayerStorage() {
		return LayerStorageType.create(getLayerStorageType(), getPooledBatchingSize());
	}

	public static void disableForceTranslucentAcceleration() {
		FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void disableCacheIdenticalPose() {
		CACHE_IDENTICAL_POSE_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void forceEnableForceTranslucentAcceleration() {
		FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableCacheIdenticalPose() {
		CACHE_IDENTICAL_POSE_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceSetForceTranslucentAcceleration(FeatureStatus status) {
		FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.push(status);
	}

	public static void forceSetCacheIdenticalPose(FeatureStatus status) {
		CACHE_IDENTICAL_POSE_CONTROLLER_STACK.push(status);
	}

	public static void forceSetDefaultLayer(int defaultLayer) {
		DEFAULT_LAYER_CONTROLLER_STACK.push(defaultLayer);
	}

	public static void forceSetDefaultLayerBeforeFunction(Runnable runnable) {
		DEFAULT_LAYER_BEFORE_FUNCTION_CONTROLLER_STACK.push(runnable);
	}

	public static void forceSetDefaultLayerAfterFunction(Runnable runnable) {
		DEFAULT_LAYER_AFTER_FUNCTION_CONTROLLER_STACK.push(runnable);
	}

	public static void resetForceTranslucentAcceleration() {
		FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.pop();
	}

	public static void resetCacheIdenticalPose() {
		CACHE_IDENTICAL_POSE_CONTROLLER_STACK.pop();
	}

	public static void resetDefaultLayer() {
		DEFAULT_LAYER_CONTROLLER_STACK.pop();
	}

	public static void resetDefaultLayerBeforeFunction() {
		DEFAULT_LAYER_BEFORE_FUNCTION_CONTROLLER_STACK.pop();
	}

	public static void resetDefaultLayerAfterFunction() {
		DEFAULT_LAYER_AFTER_FUNCTION_CONTROLLER_STACK.pop();
	}

	public static FeatureStatus getForceTranslucentAccelerationSetting() {
		return FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.isEmpty() ? getDefaultForceTranslucentAccelerationSetting() : FORCE_TRANSLUCENT_ACCELERATION_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getCacheIdenticalPoseSetting() {
		return CACHE_IDENTICAL_POSE_CONTROLLER_STACK.isEmpty() ? getDefaultCacheIdenticalPoseSetting() : CACHE_IDENTICAL_POSE_CONTROLLER_STACK.peek();
	}

	public static int getDefaultLayer() {
		return DEFAULT_LAYER_CONTROLLER_STACK.isEmpty() ? 0 : DEFAULT_LAYER_CONTROLLER_STACK.peek();
	}

	public static Runnable getDefaultLayerBeforeFunction() {
		return DEFAULT_LAYER_BEFORE_FUNCTION_CONTROLLER_STACK.isEmpty() ? Runnables.doNothing() : DEFAULT_LAYER_BEFORE_FUNCTION_CONTROLLER_STACK.peek();
	}

	public static Runnable getDefaultLayerAfterFunction() {
		return DEFAULT_LAYER_AFTER_FUNCTION_CONTROLLER_STACK.isEmpty() ? Runnables.doNothing() : DEFAULT_LAYER_AFTER_FUNCTION_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getDefaultForceTranslucentAccelerationSetting() {
		return FeatureConfig.CONFIG.coreForceTranslucentAcceleration.get();
	}

	public static FeatureStatus getDefaultCacheIdenticalPoseSetting() {
		return FeatureConfig.CONFIG.coreCacheIdenticalPose.get();
	}

	public static void setRenderingLevel() {
		RENDERING_LEVEL = true;
	}

	public static void resetRenderingLevel() {
		RENDERING_LEVEL = false;
	}

	public static void setRenderingHand() {
		RENDERING_HAND = true;
	}

	public static void resetRenderingHand() {
		RENDERING_HAND = false;
	}

	public static void setRenderingGui() {
		RENDERING_GUI = true;
	}

	public static void resetRenderingGui() {
		RENDERING_GUI = false;
	}

	public static boolean isRenderingLevel() {
		return RENDERING_LEVEL;
	}

	public static boolean isRenderingHand() {
		return RENDERING_HAND;
	}

	public static boolean isRenderingGui() {
		return RENDERING_GUI;
	}

	public static void setGuiBatching() {
		GUI_BATCHING = true;
	}

	public static void resetGuiBatching() {
		GUI_BATCHING = false;
	}

	public static boolean isGuiBatching() {
		return GUI_BATCHING;
	}
}

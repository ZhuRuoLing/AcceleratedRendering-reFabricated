package com.github.argon4w.acceleratedrendering.compat.sodium;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;

import java.util.ArrayDeque;
import java.util.Deque;

public class SodiumCompatFeature {

	public static final Deque<FeatureStatus> DISABLING_OPTIMIZED_PATH_CONTROLLER = new ArrayDeque<>();

	public static boolean isEnabled() {
		return FeatureConfig.CONFIG.sodiumCompatFeatureStatus.get() == FeatureStatus.ENABLED;
	}

	public static boolean shouldDisableOptimizedPath() {
		return getOptimizedPathDisableSetting() == FeatureStatus.ENABLED;
	}

	public static void disableOptimizedPath() {
		DISABLING_OPTIMIZED_PATH_CONTROLLER.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableOptimizedPath() {
		DISABLING_OPTIMIZED_PATH_CONTROLLER.push(FeatureStatus.DISABLED);
	}

	public static void forceForceOptimizedPathStatus(FeatureStatus status) {
		DISABLING_OPTIMIZED_PATH_CONTROLLER.push(status);
	}

	public static void resetOptimizedPathStatus() {
		DISABLING_OPTIMIZED_PATH_CONTROLLER.pop();
	}

	public static FeatureStatus getOptimizedPathDisableSetting() {
		return DISABLING_OPTIMIZED_PATH_CONTROLLER.isEmpty() ? getDefaultOptimizedPathDisableSetting() : DISABLING_OPTIMIZED_PATH_CONTROLLER.peek();
	}

	public static FeatureStatus getDefaultOptimizedPathDisableSetting() {
		return FeatureConfig.CONFIG.sodiumCompatDisableOptimizedPath.get();
	}
}

package com.github.argon4w.acceleratedrendering.compat.curios;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.features.filter.FilterType;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class CuriosCompatFeature {

	public	static final Deque	<FeatureStatus> LAYER_ACCELERATION_CONTROLLER_STACK		= new ArrayDeque<>			();
	public	static final Deque	<FeatureStatus> CURIOS_ITEM_FILTER_CONTROLLER_STACK		= new ArrayDeque<>			();
	private	static final Set	<Item>			CURIOS_ITEM_FILTER_VALUES				= new ReferenceOpenHashSet<>();

	static {
		FeatureConfig.CONFIG.curiosItemFilterValues.get().forEach(string -> CURIOS_ITEM_FILTER_VALUES.add(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(string))));
	}

	public static boolean isEnabled() {
		return FeatureConfig.CONFIG.curiosCompatFeatureStatus.get() == FeatureStatus.ENABLED;
	}

	public static boolean testCuriosItem(ItemStack itemStack) {
		return getCuriosItemFilterType().test(CURIOS_ITEM_FILTER_VALUES, itemStack.getItem());
	}

	public static boolean shouldAccelerateCurios() {
		return getLayerAccelerationSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldFilterCuriosItems() {
		return getCuriosItemFilterSetting() == FeatureStatus.ENABLED;
	}

	public static FilterType getCuriosItemFilterType() {
		return FeatureConfig.CONFIG.curiosItemFilterType.get();
	}

	public static void disableLayerAcceleration() {
		LAYER_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void disableCuriosItemFilter() {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void forceEnableLayerAcceleration() {
		LAYER_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableCuriosItemFilter() {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceSetLayerAcceleration(FeatureStatus status) {
		LAYER_ACCELERATION_CONTROLLER_STACK.push(status);
	}

	public static void forceSetCuriosItemFilter(FeatureStatus status) {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.push(status);
	}

	public static void resetLayerAcceleration() {
		LAYER_ACCELERATION_CONTROLLER_STACK.pop();
	}

	public static void resetCuriosItemFilter() {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.pop();
	}

	public static FeatureStatus getLayerAccelerationSetting() {
		return LAYER_ACCELERATION_CONTROLLER_STACK.isEmpty() ? getDefaultLayerAccelerationSettings() : LAYER_ACCELERATION_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getCuriosItemFilterSetting() {
		return CURIOS_ITEM_FILTER_CONTROLLER_STACK.isEmpty() ? getDefaultCuriosItemFilterSetting() : CURIOS_ITEM_FILTER_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getDefaultLayerAccelerationSettings() {
		return FeatureConfig.CONFIG.curiosCompatLayerAcceleration.get();
	}

	public static FeatureStatus getDefaultCuriosItemFilterSetting() {
		return FeatureConfig.CONFIG.curiosItemFilter.get();
	}
}

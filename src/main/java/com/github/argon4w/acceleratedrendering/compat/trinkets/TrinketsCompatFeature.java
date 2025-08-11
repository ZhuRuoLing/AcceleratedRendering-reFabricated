package com.github.argon4w.acceleratedrendering.compat.trinkets;

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

public class TrinketsCompatFeature {

	public	static final Deque	<FeatureStatus> LAYER_ACCELERATION_CONTROLLER_STACK		= new ArrayDeque<>			();
	public	static final Deque	<FeatureStatus> CURIOS_ITEM_FILTER_CONTROLLER_STACK		= new ArrayDeque<>			();
	private	static final Set	<Item>			CURIOS_ITEM_FILTER_VALUES				= new ReferenceOpenHashSet<>();

	static {
		FeatureConfig.CONFIG.trinketsItemFilterValues.get().forEach(string -> CURIOS_ITEM_FILTER_VALUES.add(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(string))));
	}

	public static boolean isEnabled() {
		return FeatureConfig.CONFIG.trinketsCompatFeatureStatus.get() == FeatureStatus.ENABLED;
	}

	public static boolean testTrinketsItem(ItemStack itemStack) {
		return getTrinketsItemFilterType().test(CURIOS_ITEM_FILTER_VALUES, itemStack.getItem());
	}

	public static boolean shouldAccelerateTrinkets() {
		return getLayerAccelerationSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldFilterTrinketsItems() {
		return getTrinketsItemFilterSetting() == FeatureStatus.ENABLED;
	}

	public static FilterType getTrinketsItemFilterType() {
		return FeatureConfig.CONFIG.trinketsItemFilterType.get();
	}

	public static void disableLayerAcceleration() {
		LAYER_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void disableTrinketsItemFilter() {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void forceEnableLayerAcceleration() {
		LAYER_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableTrinketsItemFilter() {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceSetLayerAcceleration(FeatureStatus status) {
		LAYER_ACCELERATION_CONTROLLER_STACK.push(status);
	}

	public static void forceSetTrinketsItemFilter(FeatureStatus status) {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.push(status);
	}

	public static void resetLayerAcceleration() {
		LAYER_ACCELERATION_CONTROLLER_STACK.pop();
	}

	public static void resetTrinketsItemFilter() {
		CURIOS_ITEM_FILTER_CONTROLLER_STACK.pop();
	}

	public static FeatureStatus getLayerAccelerationSetting() {
		return LAYER_ACCELERATION_CONTROLLER_STACK.isEmpty() ? getDefaultLayerAccelerationSettings() : LAYER_ACCELERATION_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getTrinketsItemFilterSetting() {
		return CURIOS_ITEM_FILTER_CONTROLLER_STACK.isEmpty() ? getDefaultTrinketsItemFilterSetting() : CURIOS_ITEM_FILTER_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getDefaultLayerAccelerationSettings() {
		return FeatureConfig.CONFIG.trinketsCompatLayerAcceleration.get();
	}

	public static FeatureStatus getDefaultTrinketsItemFilterSetting() {
		return FeatureConfig.CONFIG.trinketsItemFilter.get();
	}
}

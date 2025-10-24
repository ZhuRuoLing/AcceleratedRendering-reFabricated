package com.github.argon4w.acceleratedrendering.features.filter;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.core.utils.RegistryFilter;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class FilterFeature {

	private static final Deque	<FeatureStatus>			MENU_FILTER_CONTROLLER_STACK			= new ArrayDeque<>();
	private	static final Deque	<FeatureStatus>			ENTITIES_FILTER_CONTROLLER_STACK		= new ArrayDeque<>();
	private	static final Deque	<FeatureStatus>			BLOCK_ENTITIES_FILTER_CONTROLLER_STACK	= new ArrayDeque<>();
	private	static final Deque	<FeatureStatus>			ITEM_FILTER_CONTROLLER_STACK			= new ArrayDeque<>();
	private static final Deque	<FeatureStatus>			STAGE_FILTER_CONTROLLER_STACK			= new ArrayDeque<>();

	private static final Set	<MenuType<?>>			MENU_FILTER_VALUES;
	private	static final Set	<EntityType<?>>			ENTITY_FILTER_VALUES;
	private	static final Set	<BlockEntityType<?>>	BLOCK_ENTITY_FILTER_VALUES;
	private static final Set	<Item>					ITEM_FILTER_VALUES;
	private static final Set	<String>				STAGE_FILTER_VALUES;

	static {
		MENU_FILTER_VALUES			= RegistryFilter.filterValues	(BuiltInRegistries.MENU,				FeatureConfig.CONFIG.filterMenuFilterValues			.get());
		ENTITY_FILTER_VALUES		= RegistryFilter.filterValues	(BuiltInRegistries.ENTITY_TYPE,			FeatureConfig.CONFIG.filterEntityFilterValues		.get());
		BLOCK_ENTITY_FILTER_VALUES	= RegistryFilter.filterValues	(BuiltInRegistries.BLOCK_ENTITY_TYPE,	FeatureConfig.CONFIG.filterBlockEntityFilterValues	.get());
		ITEM_FILTER_VALUES			= RegistryFilter.filterValues	(BuiltInRegistries.ITEM,				FeatureConfig.CONFIG.filterItemFilterValues			.get());
		STAGE_FILTER_VALUES			= new ReferenceOpenHashSet<>	(										FeatureConfig.CONFIG.filterStageFilterValues		.get());
	}

	public static boolean isEnabled() {
		return FeatureConfig.CONFIG.filterFeatureStatus.get() == FeatureStatus.ENABLED;
	}

	public static boolean testMenu(AbstractContainerMenu menu) {
		return menu.menuType == null || getMenuFilterType().test(MENU_FILTER_VALUES, menu.menuType);
	}

	public static boolean testEntity(Entity entity) {
		return getEntityFilterType().test(ENTITY_FILTER_VALUES, entity.getType());
	}

	public static boolean testBlockEntity(BlockEntity entity) {
		return getBlockEntityFilterType().test(BLOCK_ENTITY_FILTER_VALUES, entity.getType());
	}

	public static boolean testItem(ItemStack itemStack) {
		return getItemFilterType().test(ITEM_FILTER_VALUES, itemStack.getItem());
	}

	public static boolean testStage(RenderLevelStageEvent.Stage stage) {
		return getStageFilterType().test(STAGE_FILTER_VALUES, stage.toString());
	}

	public static boolean shouldFilterMenus() {
		return getMenuFilterSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldFilterEntities() {
		return getEntityFilterSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldFilterBlockEntities() {
		return getBlockEntityFilterSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldFilterItems() {
		return getItemFilterSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldFilterStage() {
		return getStageFilterSetting() == FeatureStatus.ENABLED;
	}

	public static FilterType getMenuFilterType() {
		return FeatureConfig.CONFIG.filterMenuFilterType.get();
	}

	public static FilterType getEntityFilterType() {
		return FeatureConfig.CONFIG.filterEntityFilterType.get();
	}

	public static FilterType getBlockEntityFilterType() {
		return FeatureConfig.CONFIG.filterBlockEntityFilterType.get();
	}

	public static FilterType getItemFilterType() {
		return FeatureConfig.CONFIG.filterItemFilterType.get();
	}

	public static FilterType getStageFilterType() {
		return FeatureConfig.CONFIG.filterStageFilterType.get();
	}

	public static void disableMenuFilter() {
		MENU_FILTER_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void disableEntityFilter() {
		ENTITIES_FILTER_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void disableBlockEntityFilter() {
		BLOCK_ENTITIES_FILTER_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void disableItemFilter() {
		ITEM_FILTER_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void disableStageFilter() {
		STAGE_FILTER_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void forceEnableMenuFilter() {
		MENU_FILTER_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableEntityFilter() {
		ENTITIES_FILTER_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableBlockEntityFilter() {
		BLOCK_ENTITIES_FILTER_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableItemFilter() {
		ITEM_FILTER_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceEnableStageFilter() {
		STAGE_FILTER_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceSetMenuFilter(FeatureStatus status) {
		MENU_FILTER_CONTROLLER_STACK.push(status);
	}

	public static void forceSetEntityFilter(FeatureStatus status) {
		ENTITIES_FILTER_CONTROLLER_STACK.push(status);
	}

	public static void forceSetBlockEntityFilter(FeatureStatus status) {
		BLOCK_ENTITIES_FILTER_CONTROLLER_STACK.push(status);
	}

	public static void forceSetItemFilter(FeatureStatus status) {
		ITEM_FILTER_CONTROLLER_STACK.push(status);
	}

	public static void forceSetStageFilter(FeatureStatus status) {
		STAGE_FILTER_CONTROLLER_STACK.push(status);
	}

	public static void resetMenuFilter() {
		MENU_FILTER_CONTROLLER_STACK.pop();
	}

	public static void resetEntityFilter() {
		ENTITIES_FILTER_CONTROLLER_STACK.pop();
	}

	public static void resetBlockEntityFilter() {
		BLOCK_ENTITIES_FILTER_CONTROLLER_STACK.pop();
	}

	public static void resetItemFilter() {
		ITEM_FILTER_CONTROLLER_STACK.pop();
	}

	public static void resetStageFilter() {
		STAGE_FILTER_CONTROLLER_STACK.pop();
	}

	public static FeatureStatus getMenuFilterSetting() {
		return MENU_FILTER_CONTROLLER_STACK.isEmpty() ? getDefaultMenuFilterSetting() : MENU_FILTER_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getEntityFilterSetting() {
		return ENTITIES_FILTER_CONTROLLER_STACK.isEmpty() ? getDefaultEntityFilterSetting() : ENTITIES_FILTER_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getBlockEntityFilterSetting() {
		return BLOCK_ENTITIES_FILTER_CONTROLLER_STACK.isEmpty() ? getDefaultBlockEntityFilterSetting() : BLOCK_ENTITIES_FILTER_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getItemFilterSetting() {
		return ITEM_FILTER_CONTROLLER_STACK.isEmpty() ? getDefaultItemFilterSetting() : ITEM_FILTER_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getStageFilterSetting() {
		return STAGE_FILTER_CONTROLLER_STACK.isEmpty() ? getDefaultStageFilterSetting() : STAGE_FILTER_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getDefaultMenuFilterSetting() {
		return FeatureConfig.CONFIG.filterMenuFilter.get();
	}

	public static FeatureStatus getDefaultEntityFilterSetting() {
		return FeatureConfig.CONFIG.filterEntityFilter.get();
	}

	public static FeatureStatus getDefaultBlockEntityFilterSetting() {
		return FeatureConfig.CONFIG.filterBlockEntityFilter.get();
	}

	public static FeatureStatus getDefaultItemFilterSetting() {
		return FeatureConfig.CONFIG.filterItemFilter.get();
	}

	public static FeatureStatus getDefaultStageFilterSetting() {
		return FeatureConfig.CONFIG.filterStageFilter.get();
	}
}

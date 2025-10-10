package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.configs.FeatureConfig;
import com.github.argon4w.acceleratedrendering.configs.FeatureStatus;
import com.github.argon4w.acceleratedrendering.configs.PipelineSetting;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshType;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleTextureTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;

import java.util.ArrayDeque;
import java.util.Deque;

public class AcceleratedItemRenderingFeature {

	public	static final TextureTarget			GUI_OVERLAY_TARGET					= new SimpleTextureTarget	(false);
	private	static final Deque<PipelineSetting>	PIPELINE_CONTROLLER_STACK			= new ArrayDeque<>			();
	private	static final Deque<FeatureStatus>	BAKE_QUAD_MESH_CONTROLLER_STACK		= new ArrayDeque<>			();
	private	static final Deque<FeatureStatus>	HAND_ACCELERATION_CONTROLLER_STACK	= new ArrayDeque<>			();
	private	static final Deque<FeatureStatus>	GUI_ACCELERATION_CONTROLLER_STACK	= new ArrayDeque<>			();

	public static boolean isEnabled() {
		return FeatureConfig.CONFIG.acceleratedItemRenderingFeatureStatus.get() == FeatureStatus.ENABLED;
	}

	public static boolean shouldUseAcceleratedPipeline() {
		return getPipelineSetting() == PipelineSetting.ACCELERATED;
	}

	public static boolean shouldBakeMeshForQuad() {
		return getBakeQuadMeshSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldAccelerateInHand() {
		return getHandAccelerationSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldAccelerateInGui() {
		return getGUIAccelerationSetting() == FeatureStatus.ENABLED;
	}

	public static boolean shouldUseGuiItemBatching() {
		return FeatureConfig.CONFIG.acceleratedItemRenderingGuiItemBatching.get() == FeatureStatus.ENABLED;
	}

	public static MeshType getMeshType() {
		return FeatureConfig.CONFIG.acceleratedItemRenderingMeshType.get();
	}

	public static void useVanillaPipeline() {
		PIPELINE_CONTROLLER_STACK.push(PipelineSetting.VANILLA);
	}

	public static void dontBakeMeshForQuad() {
		BAKE_QUAD_MESH_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void dontAccelerateInHand() {
		HAND_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void dontAccelerateInGui() {
		GUI_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.DISABLED);
	}

	public static void forceUseAcceleratedPipeline() {
		PIPELINE_CONTROLLER_STACK.push(PipelineSetting.ACCELERATED);
	}

	public static void forceBakeMeshForQuad() {
		BAKE_QUAD_MESH_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceAccelerateInHand() {
		HAND_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceAccelerateInGui() {
		GUI_ACCELERATION_CONTROLLER_STACK.push(FeatureStatus.ENABLED);
	}

	public static void forceSetPipeline(PipelineSetting pipeline) {
		PIPELINE_CONTROLLER_STACK.push(pipeline);
	}

	public static void forceSetBakeQuadForMesh(FeatureStatus status) {
		BAKE_QUAD_MESH_CONTROLLER_STACK.push(status);
	}

	public static void forceSetHandAcceleration(FeatureStatus status) {
		HAND_ACCELERATION_CONTROLLER_STACK.push(status);
	}

	public static void forceSetGUIAcceleration(FeatureStatus status) {
		GUI_ACCELERATION_CONTROLLER_STACK.push(status);
	}

	public static void resetPipeline() {
		PIPELINE_CONTROLLER_STACK.pop();
	}

	public static void resetBakeQuadForMesh() {
		BAKE_QUAD_MESH_CONTROLLER_STACK.pop();
	}

	public static void resetHandAcceleration() {
		HAND_ACCELERATION_CONTROLLER_STACK.pop();
	}

	public static void resetGuiAcceleration() {
		GUI_ACCELERATION_CONTROLLER_STACK.pop();
	}

	public static PipelineSetting getPipelineSetting() {
		return PIPELINE_CONTROLLER_STACK.isEmpty() ? getDefaultPipelineSetting() : PIPELINE_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getBakeQuadMeshSetting() {
		return BAKE_QUAD_MESH_CONTROLLER_STACK.isEmpty() ? getDefaultBakeQuadMeshSetting() : BAKE_QUAD_MESH_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getHandAccelerationSetting() {
		return HAND_ACCELERATION_CONTROLLER_STACK.isEmpty() ? getDefaultHandAccelerationSetting() : HAND_ACCELERATION_CONTROLLER_STACK.peek();
	}

	public static FeatureStatus getGUIAccelerationSetting() {
		return GUI_ACCELERATION_CONTROLLER_STACK.isEmpty() ? getDefaultGUIAccelerationSetting() : GUI_ACCELERATION_CONTROLLER_STACK.peek();
	}

	public static PipelineSetting getDefaultPipelineSetting() {
		return FeatureConfig.CONFIG.acceleratedItemRenderingDefaultPipeline.get();
	}

	public static FeatureStatus getDefaultBakeQuadMeshSetting() {
		return FeatureConfig.CONFIG.acceleratedItemRenderingBakeMeshForQuads.get();
	}

	public static FeatureStatus getDefaultHandAccelerationSetting() {
		return FeatureConfig.CONFIG.acceleratedItemRenderingHandAcceleration.get();
	}

	public static FeatureStatus getDefaultGUIAccelerationSetting() {
		return FeatureConfig.CONFIG.acceleratedItemRenderingGuiAcceleration.get();
	}
}

package com.github.argon4w.acceleratedrendering.core.programs.overrides;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class LoadShaderProgramOverridesEvent extends Event implements IModBusEvent {

	private final VertexFormat															vertexFormat;
	private final Object2ObjectOpenHashMap<RenderType, ITransformShaderProgramOverride> transformOverrides;
	private final Object2ObjectOpenHashMap<RenderType, IUploadingShaderProgramOverride>	uploadingOverrides;

	public LoadShaderProgramOverridesEvent(VertexFormat vertexFormat) {
		this.vertexFormat		= vertexFormat;
		this.transformOverrides	= new Object2ObjectOpenHashMap<>();
		this.uploadingOverrides	= new Object2ObjectOpenHashMap<>();
	}

	public void loadFor(
			VertexFormat			vertexFormat,
			RenderType				renderType,
			IShaderProgramOverride	override
	) {
		if (this.vertexFormat == vertexFormat) {
					if (override instanceof ITransformShaderProgramOverride transform)	transformOverrides.put					(renderType, transform);
			else	if (override instanceof IUploadingShaderProgramOverride uploading)	uploadingOverrides.put					(renderType, uploading);
			else																		throw new UnsupportedOperationException	("Unsupported override type: " + override.getClass().getSimpleName());
		}
	}

	public IShaderProgramOverrides getOverrides(ITransformShaderProgramOverride defaultTransformOverride, IUploadingShaderProgramOverride defaultUploadingOverride) {
		return new ProgramOverrides(
				transformOverrides,
				uploadingOverrides,
				defaultTransformOverride,
				defaultUploadingOverride
		);
	}

	@Getter
	public static class ProgramOverrides implements IShaderProgramOverrides {

		private final Object2ObjectOpenHashMap<RenderType, ITransformShaderProgramOverride> transformOverrides;
		private final Object2ObjectOpenHashMap<RenderType, IUploadingShaderProgramOverride>	uploadingOverrides;

		public ProgramOverrides(
				Object2ObjectOpenHashMap<RenderType, ITransformShaderProgramOverride>	transformOverrides,
				Object2ObjectOpenHashMap<RenderType, IUploadingShaderProgramOverride>	uploadingOverrides,
				ITransformShaderProgramOverride											defaultTransformOverride,
				IUploadingShaderProgramOverride											defaultUploadingOverride
		) {
			this.transformOverrides = transformOverrides;
			this.uploadingOverrides = uploadingOverrides;

			this.transformOverrides.defaultReturnValue(defaultTransformOverride);
			this.uploadingOverrides.defaultReturnValue(defaultUploadingOverride);
		}
	}
}

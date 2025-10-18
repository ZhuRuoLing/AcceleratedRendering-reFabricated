package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.IAcceleratedBufferSource;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.AcceleratedBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.Set;

public class AcceleratedBufferSources implements IAcceleratedBufferSource {

	private final Map<VertexFormat, AcceleratedBufferSource>	sources;
	private final Set<VertexFormat.Mode>						validModes;
	private final Set<String>									dynamicNames;
	private final boolean										canSort;
	private final boolean										canScroll;

	private AcceleratedBufferSources(
			Map<VertexFormat, AcceleratedBufferSource>	sources,
			Set<VertexFormat.Mode>						validModes,
			Set<String>									dynamicNames,
			boolean										canSort,
			boolean										canScroll
	) {
		this.sources		= sources;
		this.validModes		= validModes;
		this.dynamicNames	= dynamicNames;
		this.canSort		= canSort;
		this.canScroll		= canScroll;
	}

	@Override
	public AcceleratedBufferBuilder getBuffer(
			RenderType	renderType,
			Runnable	before,
			Runnable	after,
			int			layer
	) {
		if (			renderType		!= null
				&& 	(	CoreFeature		.shouldForceAccelerateTranslucent	()	|| canSort		|| !						renderType.sortOnUpload)
				&& 	(	CoreFeature		.shouldCacheDynamicRenderType		()	|| canScroll	|| !dynamicNames.contains(	renderType.name))
				&&		validModes		.contains							(renderType.mode())
				&&		sources			.containsKey						(renderType.format())
		) {
			return sources
					.get		(renderType.format())
					.getBuffer	(
							renderType,
							before,
							after,
							layer
					);
		}

		return null;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final	Map<VertexFormat, AcceleratedBufferSource>	sources;
		private final	Set<VertexFormat.Mode>						validModes;
		private final	Set<String>									dynamicNames;

		private			boolean										canSort;
		private			boolean										canScroll;

		private Builder() {
			this.sources		= new Reference2ObjectOpenHashMap	<>();
			this.validModes		= new ReferenceOpenHashSet			<>();
			this.dynamicNames	= new ObjectOpenHashSet				<>();

			this.canSort		= false;
			this.canScroll		= false;
		}

		public Builder source(AcceleratedBufferSource bufferSource) {
			for (var format : bufferSource
					.getEnvironment		()
					.getVertexFormats	()
			) {
				sources.put(format, bufferSource);
			}

			return this;
		}

		public Builder mode(VertexFormat.Mode mode) {
			validModes.add(mode);
			return this;
		}

		public Builder dynamic(String name) {
			dynamicNames.add(name);
			return this;
		}

		public Builder canSort() {
			canSort = true;
			return this;
		}

		public Builder canScroll() {
			canScroll = true;
			return this;
		}

		public AcceleratedBufferSources build() {
			return new AcceleratedBufferSources(
					sources,
					validModes,
					dynamicNames,
					canSort,
					canScroll
			);
		}
	}
}

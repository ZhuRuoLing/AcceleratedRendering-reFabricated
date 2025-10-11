package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes;

import com.github.argon4w.acceleratedrendering.core.utils.SimpleCachedArray;
import lombok.Getter;

@Getter
public class SimpleMeshInfo implements SimpleCachedArray.Element {

	private int color;
	private int light;
	private int overlay;
	private int sharing;
	private int shouldCull;

	public SimpleMeshInfo() {
		this.color		= -1;
		this.light		= -1;
		this.overlay	= -1;
		this.sharing	= -1;
	}

	public void setupMeshInfo(
			int color,
			int light,
			int overlay,
			int sharing,
			int shouldCull
	) {
		this.color		= color;
		this.light		= light;
		this.overlay	= overlay;
		this.sharing	= sharing;
		this.shouldCull	= shouldCull;
	}

	@Override
	public void reset() {

	}

	@Override
	public void delete() {

	}
}

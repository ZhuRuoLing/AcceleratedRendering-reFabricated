package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes;

public enum MeshInfoCacheType {

	SIMPLE,
	HANDLE,
	UNSAFE;

	public IMeshInfoCache create() {
		return create(this);
	}

	public static IMeshInfoCache create(MeshInfoCacheType type) {
		return switch (type) {
			case SIMPLE -> new SimpleMeshInfoCache			();
			case HANDLE -> new FlattenMeshInfoCache			();
			case UNSAFE -> new UnsafeMemoryMeshInfoCache	();
		};
	}
}

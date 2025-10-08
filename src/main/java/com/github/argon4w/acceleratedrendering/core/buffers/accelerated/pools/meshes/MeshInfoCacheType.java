package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes;

public enum MeshInfoCacheType {

	SIMPLE,
	UNSAFE,
	HANDLE;

	public static IMeshInfoCache create(MeshInfoCacheType type) {
		return switch (type) {
			case SIMPLE -> new SimpleMeshInfoCache			();
			case UNSAFE -> new UnsafeMemoryMeshInfoCache	();
			case HANDLE -> new FlattenVarHandleMeshInfoCache();
		};
	}
}

package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.backends.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryInterface;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.SimpleDynamicMemoryInterface;
import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.github.argon4w.acceleratedrendering.core.programs.overrides.IUploadingShaderProgramOverride;
import com.github.argon4w.acceleratedrendering.core.utils.FastColorUtils;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleResetPool;
import lombok.Getter;
import lombok.Setter;

import java.util.function.IntFunction;
import java.util.function.LongSupplier;

import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class MeshUploaderPool extends SimpleResetPool<MeshUploaderPool.MeshUploader, Void> {

	public MeshUploaderPool() {
		super(128, null);
	}

	@Override
	protected MeshUploader create(Void context, int i) {
		return new MeshUploader();
	}

	@Override
	protected void reset(MeshUploader meshUploader) {
		meshUploader.reset();
	}

	@Override
	protected void delete(MeshUploader meshUploader) {
		meshUploader.delete();
	}

	@Override
	public MeshUploader fail() {
		expand();
		return get();
	}

	public static class MeshUploader implements IntFunction<MeshInfo>, LongSupplier {

		public static	final	int								MESH_INFO_BUFFER_INDEX	= 8;

		public			final	IMemoryInterface				meshInfoSharing;
		public			final	IMemoryInterface				meshInfoNoCull;
		public			final	IMemoryInterface				meshInfoColor;
		public			final	IMemoryInterface				meshInfoOverlay;
		public			final	IMemoryInterface				meshInfoLight;

		private			final	MappedBuffer 					meshInfoBuffer;
		@Getter private	final	IMeshInfoCache					meshInfos;

		@Getter	@Setter private	ServerMesh						serverMesh;
		@Getter @Setter private IUploadingShaderProgramOverride	uploadingOverride;

		public MeshUploader() {
			this.meshInfoSharing	= new SimpleDynamicMemoryInterface	(0L * 4L, this);
			this.meshInfoNoCull		= new SimpleDynamicMemoryInterface	(1L * 4L, this);
			this.meshInfoColor		= new SimpleDynamicMemoryInterface	(2L * 4L, this);
			this.meshInfoOverlay	= new SimpleDynamicMemoryInterface	(3L * 4L, this);
			this.meshInfoLight		= new SimpleDynamicMemoryInterface	(4L * 4L, this);

			this.meshInfoBuffer		= new MappedBuffer					(64L);
			this.meshInfos			= CoreFeature.createMeshInfoCache	();

			this.serverMesh			= null;
			this.uploadingOverride	= null;
		}

		public void addUpload(
				int color,
				int light,
				int overlay,
				int sharing,
				int shouldCull
		) {
			meshInfos.setup(
					color,
					light,
					overlay,
					sharing,
					shouldCull
			);
		}

		public void upload() {
			var meshCount			= meshInfos		.getMeshCount	();
			var meshInfoAddress		= meshInfoBuffer.reserve		(getAsLong() * meshCount);

			for (var i = 0; i < meshCount; i ++) {
				meshInfoSharing		.at(i)	.putInt			(meshInfoAddress, meshInfos			.getSharing		(i));
				meshInfoNoCull		.at(i)	.putInt			(meshInfoAddress, meshInfos			.getShouldCull	(i));
				meshInfoColor		.at(i)	.putInt			(meshInfoAddress, FastColorUtils	.abgr32			(meshInfos.getColor(i)));
				meshInfoOverlay		.at(i)	.putInt			(meshInfoAddress, meshInfos			.getOverlay		(i));
				meshInfoLight		.at(i)	.putInt			(meshInfoAddress, meshInfos			.getLight		(i));
				uploadingOverride			.uploadMeshInfo	(meshInfoAddress, i);
			}
		}

		public void bindBuffers() {
			meshInfoBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, MESH_INFO_BUFFER_INDEX);
		}

		public void reset() {
			meshInfos		.reset();
			meshInfoBuffer	.reset();
		}

		public void delete() {
			meshInfos		.delete();
			meshInfoBuffer	.delete();
		}

		@Override
		public MeshInfo apply(int value) {
			return new MeshInfo();
		}

		@Override
		public long getAsLong() {
			return uploadingOverride.getMeshInfoSize();
		}
	}
}

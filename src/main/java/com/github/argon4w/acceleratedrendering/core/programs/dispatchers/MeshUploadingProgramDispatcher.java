package com.github.argon4w.acceleratedrendering.core.programs.dispatchers;

import com.github.argon4w.acceleratedrendering.core.backends.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.backends.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.backends.programs.Uniform;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.AcceleratedRingBuffers;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools.meshes.MeshUploaderPool;
import com.github.argon4w.acceleratedrendering.core.programs.ComputeShaderProgramLoader;
import com.github.argon4w.acceleratedrendering.core.programs.overrides.IUploadingShaderProgramOverride;
import com.github.argon4w.acceleratedrendering.core.utils.FastColorUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL46.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL46.glMemoryBarrier;

public class MeshUploadingProgramDispatcher {

	private static	final	int														GROUP_SIZE					= 128;
	private static	final	int														DISPATCH_COUNT_Y_Z			= 1;
	public static	final	int														SPARSE_MESH_BUFFER_INDEX	= 5;
	public static	final	int														MESH_BUFFER_INDEX			= 7;

	private			final	Map<IServerBuffer, List<MeshUploaderPool.MeshUploader>>	denseUploaders;
	private			final	Map<IServerBuffer, List<MeshUploaderPool.MeshUploader>>	sparseUploaders;

	private					IUploadingShaderProgramOverride							lastOverride;

	public MeshUploadingProgramDispatcher() {
		this.denseUploaders		= new Reference2ObjectLinkedOpenHashMap<>();
		this.sparseUploaders	= new Reference2ObjectLinkedOpenHashMap<>();
		this.lastOverride		= null;
	}

	public void dispatch(Collection<AcceleratedBufferBuilder> builders, AcceleratedRingBuffers.Buffers buffer) {
		lastOverride	= null;
		var barriers	= 0;
		var transform	= buffer
				.getBufferEnvironment				()
				.selectTransformProgramDispatcher	();

		for (var builder : builders) {
			var vertexBuffer	= builder	.getVertexBuffer	();
			var varyingBuffer	= builder	.getVaryingBuffer	();
			var meshVertexCount = builder	.getMeshVertexCount	();

			vertexBuffer					.reserve			(meshVertexCount * builder.getVertexSize	());
			varyingBuffer					.reserve			(meshVertexCount * builder.getVaryingSize	());
			vertexBuffer					.allocateOffset		();
			varyingBuffer					.allocateOffset		();
		}

		buffer.prepare				();
		buffer.bindTransformBuffers	();

		for (var builder : builders) {
			var offset			= 0;
			var sparseStart		= 0;

			var vertexBuffer	= builder		.getVertexBuffer	();
			var varyingBuffer	= builder		.getVaryingBuffer	();
			var vertexCount		= builder		.getVertexCount		();

			var vertexAddress	= vertexBuffer	.getCurrent			();
			var varyingAddress	= varyingBuffer	.getCurrent			();
			var vertexOffset	= vertexBuffer	.getOffset			() / builder.getVertexSize	();
			var varyingOffset	= varyingBuffer	.getOffset			() / builder.getVaryingSize	();

			for (var uploader : builder
					.getMeshUploaders	()
					.values				()
			) {
				var serverMesh	= uploader					.getServerMesh	();
				var meshCount	= uploader.getMeshInfos()	.getMeshCount	();
				var meshDense	= serverMesh				.forceDense		() || meshCount >= 128;
				var meshBuffer	= serverMesh				.meshBuffer		();
				var dense		= denseUploaders			.get			(meshBuffer);
				var sparse		= sparseUploaders			.get			(meshBuffer);

				if (dense == null) {
					dense	= new ReferenceArrayList<>	();
					sparse	= new ReferenceArrayList<>	();
					denseUploaders	.put				(meshBuffer, dense);
					sparseUploaders	.put				(meshBuffer, sparse);
				}

				(meshDense
						? dense
						: sparse).add(uploader);
			}

			for (		var meshBuffer	: sparseUploaders.keySet()) {
				for (	var uploader	: sparseUploaders.get	(meshBuffer)) {
					var mesh		= uploader	.getServerMesh	();
					var meshInfos	= uploader	.getMeshInfos	();
					var meshCount	= meshInfos	.getMeshCount	();
					var meshSize	= mesh		.size			();

					for (var i = 0; i < meshCount; i ++) {
						builder.getColorOffset		().at(offset)	.putInt			(vertexAddress, FastColorUtils	.convert		(meshInfos.getColor(i)));
						builder.getUv1Offset		().at(offset)	.putInt			(vertexAddress, meshInfos		.getOverlay		(i));
						builder.getUv2Offset		().at(offset)	.putInt			(vertexAddress, meshInfos		.getLight		(i));

						builder.getVaryingSharing	().at(offset)	.putInt			(varyingAddress, meshInfos		.getSharing		(i));
						builder.getVaryingMesh		().at(offset)	.putInt			(varyingAddress, mesh			.offset			());
						builder.getVaryingShouldCull().at(offset)	.putInt			(varyingAddress, meshInfos		.getShouldCull	(i));
						builder.getTransformOverride()				.uploadVarying	(varyingAddress, offset);

						for (var offsetValue = 0; offsetValue < meshSize; offsetValue ++) {
							builder
									.getVaryingOffset	()
									.at					(offset)
									.at					(offsetValue)
									.putInt				(varyingAddress, offsetValue);
						}

						offset += meshSize;
					}
				}

				var count = offset - sparseStart;

				if (count != 0) {
					lastOverride = null;

					meshBuffer				.bindBase(GL_SHADER_STORAGE_BUFFER, SPARSE_MESH_BUFFER_INDEX);
					barriers |= transform	.dispatch(
							builder,
							vertexBuffer,
							varyingBuffer,
							count,
							sparseStart + vertexCount + vertexOffset,
							sparseStart + vertexCount + varyingOffset
					);
				}

				sparseStart = offset;
			}

			for (var meshBuffer : denseUploaders.keySet()) {
				meshBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, MESH_BUFFER_INDEX);

				for (var uploader : denseUploaders.get(meshBuffer)) {
					var currentOverride	= builder					.getUploadingOverride	();
					var meshCount		= uploader.getMeshInfos()	.getMeshCount			();
					var mesh			= uploader					.getServerMesh			();
					var meshSize		= mesh						.size					();
					var uploadSize		= meshCount * meshSize;

					if (lastOverride == null) {
						lastOverride = currentOverride;
						lastOverride.useProgram		();
						lastOverride.setupProgram	();
					}

					transform					.resetOverride		();
					uploader					.upload				();
					uploader					.bindBuffers		();
					barriers |= currentOverride	.dispatchUploading	(
							uploadSize,
							meshCount,
							meshSize,
							(int) (offset + vertexCount + vertexOffset),
							(int) (offset + vertexCount + varyingOffset),
							(int) mesh.offset()
					);

					offset += uploadSize;
				}
			}

			for (var meshBuffer : denseUploaders.keySet()) {
				denseUploaders	.get(meshBuffer).clear();
				sparseUploaders	.get(meshBuffer).clear();
			}
		}

		glMemoryBarrier(barriers);
	}

	public void resetOverride() {
		lastOverride = null;
	}

	public static class Default implements IUploadingShaderProgramOverride {

		private final long				meshInfoSize;
		private final ComputeProgram	program;
		private final Uniform			meshCountUniform;
		private final Uniform			meshSizeUniform;
		private final Uniform			vertexOffsetUniform;
		private final Uniform			varyingOffsetUniform;
		private final Uniform			meshOffsetUniform;

		public Default(ResourceLocation key, long meshInfoSize) {
			this.meshInfoSize			= meshInfoSize;
			this.program				= ComputeShaderProgramLoader.getProgram(key);
			this.meshCountUniform		= program					.getUniform("meshCount");
			this.meshSizeUniform		= program					.getUniform("meshSize");
			this.vertexOffsetUniform	= program					.getUniform("vertexOffset");
			this.varyingOffsetUniform	= program					.getUniform("varyingOffset");
			this.meshOffsetUniform		= program					.getUniform("meshOffset");
		}

		@Override
		public long getMeshInfoSize() {
			return meshInfoSize;
		}

		@Override
		public void useProgram() {
			program.useProgram();
		}

		@Override
		public void setupProgram() {
			program.setup();
		}

		@Override
		public void uploadMeshInfo(long meshInfoAddress, int meshInfoIndex) {

		}

		@Override
		public int dispatchUploading(
				int uploadSize,
				int meshCount,
				int meshSize,
				int vertexOffset,
				int varyingOffset,
				int meshOffset
		) {
			meshCountUniform	.uploadUnsignedInt	(meshCount);
			meshSizeUniform		.uploadUnsignedInt	(meshSize);
			vertexOffsetUniform	.uploadUnsignedInt	(vertexOffset);
			varyingOffsetUniform.uploadUnsignedInt	(varyingOffset);
			meshOffsetUniform	.uploadUnsignedInt	(meshOffset);
			program				.dispatch			(
					(uploadSize + GROUP_SIZE - 1) / GROUP_SIZE,
					DISPATCH_COUNT_Y_Z,
					DISPATCH_COUNT_Y_Z
			);

			return program.getBarrierFlags();
		}
	}
}

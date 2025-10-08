package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.pools;

import com.github.argon4w.acceleratedrendering.core.backends.buffers.MappedBuffer;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.IMemoryInterface;
import com.github.argon4w.acceleratedrendering.core.buffers.memory.SimpleMemoryInterface;
import com.github.argon4w.acceleratedrendering.core.utils.SimpleResetPool;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.RenderType;

import static org.lwjgl.opengl.GL46.*;

public class DrawContextPool extends SimpleResetPool<DrawContextPool.DrawContext, MappedBuffer> {

	public DrawContextPool(int size) {
		super(size, new MappedBuffer(20L * size));
	}

	@Override
	protected DrawContext create(MappedBuffer buffer, int i) {
		return new DrawContext(i);
	}

	@Override
	protected void reset(DrawContext drawContext) {

	}

	@Override
	protected void delete(DrawContext drawContext) {

	}

	@Override
	public void delete() {
		getContext().delete();
	}

	@Override
	public DrawContext fail() {
		expand();
		return get();
	}

	@Getter
	@Setter
	public class DrawContext implements Comparable<DrawContext> {

		public static	final	int					ELEMENT_COUNT_INDEX		= 0;
		public static	final	IMemoryInterface	INDIRECT_COUNT			= new SimpleMemoryInterface(0L * 4L, 4);
		public static	final	IMemoryInterface	INDIRECT_INSTANCE_COUNT	= new SimpleMemoryInterface(1L * 4L, 4);
		public static	final	IMemoryInterface	INDIRECT_FIRST_INDEX	= new SimpleMemoryInterface(2L * 4L, 4);
		public static	final	IMemoryInterface	INDIRECT_BASE_INDEX		= new SimpleMemoryInterface(3L * 4L, 4);
		public static	final	IMemoryInterface	INDIRECT_BASE_INSTANCE	= new SimpleMemoryInterface(4L * 4L, 4);

		private			final	long				commandOffset;
		private					RenderType			renderType;

		public DrawContext(int index) {
			this.commandOffset	= index * 20L;
			this.renderType		= null;

			var address			= context		.reserve(20L);

			INDIRECT_COUNT						.putInt	(address, 0);
			INDIRECT_INSTANCE_COUNT				.putInt	(address, 1);
			INDIRECT_FIRST_INDEX				.putInt	(address, 0);
			INDIRECT_BASE_INDEX					.putInt	(address, 0);
			INDIRECT_BASE_INSTANCE				.putInt	(address, 0);
		}

		public void bindComputeBuffers(ElementBufferPool.ElementSegment elementSegmentIn) {
			var elementOffset	= elementSegmentIn	.getOffset	();
			var commandAddress	= context			.addressAt	(commandOffset);

			INDIRECT_COUNT		.putInt		(commandAddress, 0);
			INDIRECT_FIRST_INDEX.putInt		(commandAddress, elementOffset / 4);
			context				.bindRange	(
					GL_ATOMIC_COUNTER_BUFFER,
					ELEMENT_COUNT_INDEX,
					commandOffset,
					4
			);
		}

		public void drawElements(VertexFormat.Mode mode) {
			glDrawElementsIndirect(
					mode.asGLMode,
					GL_UNSIGNED_INT,
					commandOffset
			);
		}

		@Override
		public int compareTo(DrawContext that) {
			return Boolean.compare(
					this.renderType.sortOnUpload,
					that.renderType.sortOnUpload
			);
		}
	}
}

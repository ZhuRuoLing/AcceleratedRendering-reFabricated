package com.github.argon4w.acceleratedrendering.core.utils;

import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class ByteBufferBuilder implements AutoCloseable {

	private long	address;
	private int		position;

	public ByteBufferBuilder(int capacity) {
		this.address = MemoryUtil.nmemAlloc(capacity);
	}

	public long reserve(int bytes) {
		var oldPosition = position;
		var newPosition = oldPosition + bytes;

		this.resize(Integer.highestOneBit(newPosition) << 1);
		this.position = newPosition;

		return address + oldPosition;
	}

	private void resize(int newSize) {
		address = MemoryUtil.nmemRealloc(address, newSize);
	}

	@Nullable
	public ByteBufferBuilder.Result build() {
		return position > 0 ? new ByteBufferBuilder.Result(position) : null;
	}

	private void free() {
		position = 0;
	}

	@Override
	public void close() {
		MemoryUtil.nmemFree(address);
	}

	public class Result implements AutoCloseable {

		private final int capacity;

		Result(int capacity) {
			this.capacity = capacity;
		}

		public ByteBuffer byteBuffer() {
			return MemoryUtil.memByteBuffer(address, capacity);
		}

		@Override
		public void close() {
			free();
		}
	}
}

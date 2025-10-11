package com.github.argon4w.acceleratedrendering.core.buffers.blocks;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.lwjgl.opengl.GL46.*;

@Getter
@AllArgsConstructor
public enum BufferBlockType {

	SHADER_STORAGE(
			GL_SHADER_STORAGE_BUFFER,
			GL_SHADER_STORAGE_BUFFER_BINDING,
			GL_SHADER_STORAGE_BUFFER_START,
			GL_SHADER_STORAGE_BUFFER_SIZE
	),

	ATOMIC_COUNTER(
			GL_ATOMIC_COUNTER_BUFFER,
			GL_ATOMIC_COUNTER_BUFFER_BINDING,
			GL_ATOMIC_COUNTER_BUFFER_START,
			GL_ATOMIC_COUNTER_BUFFER_SIZE
	);

	private final int bindingBlock;
	private final int bufferParam;
	private final int offsetParam;
	private final int sizeParam;
}

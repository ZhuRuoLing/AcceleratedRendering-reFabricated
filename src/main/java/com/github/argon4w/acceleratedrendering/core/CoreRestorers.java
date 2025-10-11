package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.core.buffers.blocks.IBlockBufferBindingRestorer;

public class CoreRestorers {

	public	static final	IBlockBufferBindingRestorer	SHADER_STORAGE_BUFFER_RESTORER	= CoreFeature.createShaderStorageRestorer();
	public	static final	IBlockBufferBindingRestorer	ATOMIC_COUNTER_BUFFER_RESTORER	= CoreFeature.createAtomicCounterRestorer();
	private	static			boolean						BUFFER_BINDING_RECORDED			= false;

	public static void record() {
		if (CoreFeature.shouldRestoreBlockBuffers() && !BUFFER_BINDING_RECORDED) {
			SHADER_STORAGE_BUFFER_RESTORER.record();
			ATOMIC_COUNTER_BUFFER_RESTORER.record();

			BUFFER_BINDING_RECORDED = true;
		}
	}

	public static void restore() {
		if (CoreFeature.shouldRestoreBlockBuffers() && BUFFER_BINDING_RECORDED) {
			SHADER_STORAGE_BUFFER_RESTORER.restore();
			ATOMIC_COUNTER_BUFFER_RESTORER.restore();

			BUFFER_BINDING_RECORDED = false;
		}
	}

	public static void delete() {
		SHADER_STORAGE_BUFFER_RESTORER.delete();
		ATOMIC_COUNTER_BUFFER_RESTORER.delete();
	}
}

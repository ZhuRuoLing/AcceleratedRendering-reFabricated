package com.github.argon4w.acceleratedrendering.core;

import com.github.argon4w.acceleratedrendering.core.buffers.blocks.states.IBlockBufferBindingState;

public class CoreStates {

	public	static final	IBlockBufferBindingState	SHADER_STORAGE_BUFFER_STATE		= CoreFeature.createShaderStorageState();
	public	static final	IBlockBufferBindingState	ATOMIC_COUNTER_BUFFER_STATE		= CoreFeature.createAtomicCounterState();
	private	static			boolean						BUFFER_BINDING_STATE_RECORDED	= false;

	public static void record() {
		if (CoreFeature.shouldRestoreBlockBuffers() && !BUFFER_BINDING_STATE_RECORDED) {
			SHADER_STORAGE_BUFFER_STATE.record();
			ATOMIC_COUNTER_BUFFER_STATE.record();

			BUFFER_BINDING_STATE_RECORDED = true;
		}
	}

	public static void restore() {
		if (CoreFeature.shouldRestoreBlockBuffers() && BUFFER_BINDING_STATE_RECORDED) {
			SHADER_STORAGE_BUFFER_STATE.restore();
			ATOMIC_COUNTER_BUFFER_STATE.restore();

			BUFFER_BINDING_STATE_RECORDED = false;
		}
	}

	public static void delete() {
		SHADER_STORAGE_BUFFER_STATE.delete();
		ATOMIC_COUNTER_BUFFER_STATE.delete();
	}
}

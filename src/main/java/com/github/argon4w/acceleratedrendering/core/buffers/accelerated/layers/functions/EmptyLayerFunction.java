package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.functions;

public class EmptyLayerFunction implements ILayerFunction {

	public static final ILayerFunction INSTANCE = new EmptyLayerFunction();

	@Override
	public void addBefore(Runnable before) {

	}

	@Override
	public void addAfter(Runnable after) {

	}

	@Override
	public void runBefore() {

	}

	@Override
	public void runAfter() {

	}

	@Override
	public void reset() {

	}
}

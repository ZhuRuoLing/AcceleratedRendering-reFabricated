package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.functions;

public interface ILayerFunction {

	void addBefore	(Runnable before);
	void addAfter	(Runnable after);
	void runBefore	();
	void runAfter	();
	void reset		();
}

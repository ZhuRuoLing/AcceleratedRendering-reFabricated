package com.github.argon4w.acceleratedrendering.core.buffers.accelerated.layers.functions;

import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;

import java.util.Set;
import java.util.function.Consumer;

public class CustomLayerFunction implements ILayerFunction, Consumer<Runnable> {

	private final Set<Runnable> before;
	private final Set<Runnable> after;

	public CustomLayerFunction() {
		this.before	= new ReferenceLinkedOpenHashSet<>();
		this.after	= new ReferenceLinkedOpenHashSet<>();
	}

	@Override
	public void addBefore(Runnable before) {
		this.before.add(before);
	}

	@Override
	public void addAfter(Runnable after) {
		this.after.add(after);
	}

	@Override
	public void runBefore() {
		before.forEach(this);
	}

	@Override
	public void runAfter() {
		after.forEach(this);
	}

	@Override
	public void reset() {
		before	.clear();
		after	.clear();
	}

	@Override
	public void accept(Runnable runnable) {
		runnable.run();
	}
}

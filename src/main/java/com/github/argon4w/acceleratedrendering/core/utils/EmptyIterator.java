package com.github.argon4w.acceleratedrendering.core.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator<T> implements Iterator<T> {

	public static final EmptyIterator<?> INSTANCE = new EmptyIterator<>();

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		throw new NoSuchElementException();
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> of() {
		return (Iterator<T>) INSTANCE;
	}
}

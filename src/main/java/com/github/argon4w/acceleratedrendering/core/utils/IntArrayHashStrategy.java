package com.github.argon4w.acceleratedrendering.core.utils;

import it.unimi.dsi.fastutil.Hash;

import java.util.Arrays;

public class IntArrayHashStrategy implements Hash.Strategy<int[]> {

	public static final IntArrayHashStrategy INSTANCE = new IntArrayHashStrategy();

	@Override
	public int hashCode(int[] o) {
		return Arrays.hashCode(o);
	}

	@Override
	public boolean equals(int[] a, int[] b) {
		return Arrays.equals(a, b);
	}
}

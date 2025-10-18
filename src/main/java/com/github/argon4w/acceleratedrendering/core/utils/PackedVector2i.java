package com.github.argon4w.acceleratedrendering.core.utils;

public class PackedVector2i {

	public static int pack(float u, float v) {
		return pack((int) u, (int) v);
	}

	public static int pack(int u, int v) {
		return (u & 0xFFFF) | (v & 0xFFFF) << 16;
	}

	public static int unpackU(int packed) {
		return packed & 0xFFFF;
	}

	public static int unpackV(int packed) {
		return packed >>> 16 & 0xFFFF;
	}
}

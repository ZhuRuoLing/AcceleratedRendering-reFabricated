package com.github.argon4w.acceleratedrendering.core.utils;

public class FastColorUtils {

	public static int abgr32(int argb32) {
		return		argb32 & 0xFF00FF00
				| (	argb32 & 0x00FF0000) >> 16
				| (	argb32 & 0x000000FF) << 16;
	}

	public static int opaque(int rgba32) {
		return (255 << 24) | rgba32;
	}
}

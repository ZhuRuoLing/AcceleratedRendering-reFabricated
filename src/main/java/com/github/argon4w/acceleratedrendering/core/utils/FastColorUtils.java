package com.github.argon4w.acceleratedrendering.core.utils;

public class FastColorUtils {

	public static int convert(int color) {
		return		color & 0xFF00FF00
				| (	color & 0x00FF0000) >> 16
				| (	color & 0x000000FF) << 16;
	}

	public static int opaque(int rgba32) {
		return (255 << 24) | rgba32;
	}
}

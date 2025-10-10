package com.github.argon4w.acceleratedrendering.core.utils;

import net.minecraft.util.FastColor;

public class FastColorUtils {

	public static int abgr32(int rgba32) {
		return FastColor.ABGR32.color(
				FastColor.ARGB32.alpha	(rgba32),
				FastColor.ARGB32.blue	(rgba32),
				FastColor.ARGB32.green	(rgba32),
				FastColor.ARGB32.red	(rgba32)
		);
	}

	public static int opaque(int rgba32) {
		return (255 << 24) | rgba32;
	}
}

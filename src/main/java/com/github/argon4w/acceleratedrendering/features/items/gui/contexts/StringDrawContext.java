package com.github.argon4w.acceleratedrendering.features.items.gui.contexts;

import net.minecraft.client.gui.Font;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public record StringDrawContext(
		Matrix4f	transform,
		Matrix3f	normal,
		Font		font,
		String		text,
		int			textX,
		int			textY,
		int			textColor,
		boolean		dropShadow
) {

}

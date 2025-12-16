package com.github.argon4w.acceleratedrendering.features.ftb.mixins;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GuiHelper.class)
public class GuiHelperMixin {

	/**
	 * @author Argon4W
	 * @reason Use modern rendering code
	 */
	@Overwrite(remap = false)
	public static void drawItem(
			GuiGraphics	graphics,
			ItemStack	stack,
			int			hash,
			boolean		renderOverlay,
			String		text
	) {
		if (!stack.isEmpty()) {
			var pose = graphics.pose();

			pose.pushPose	();
			pose.translate	(
					-8.0F,
					-8.0F,
					-8.0F
			);

			pose
					.last		()
					.normal		()
					.identity	();

			graphics.renderItem(stack, 0, 0);

			if (renderOverlay) {
				graphics.renderItemDecorations(
						Minecraft.getInstance().font,
						stack,
						0,
						0,
						text
				);
			}

			graphics.pose().popPose();
		}
	}
}

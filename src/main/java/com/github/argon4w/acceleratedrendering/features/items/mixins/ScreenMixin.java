package com.github.argon4w.acceleratedrendering.features.items.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public class ScreenMixin {

	@Shadow @Nullable protected Minecraft minecraft;
	@Shadow protected Font font;
}

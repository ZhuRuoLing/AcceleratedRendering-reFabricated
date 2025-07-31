package com.github.argon4w.acceleratedrendering.core.utils;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL46.*;


public class TextureUtils implements ResourceManagerReloadListener {

	public 	static	final TextureUtils													INSTANCE	= new TextureUtils						();
	private	static	final Object2ObjectLinkedOpenHashMap<ResourceLocation, NativeImage> IMAGE_CACHE	= new Object2ObjectLinkedOpenHashMap<>	();

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		IMAGE_CACHE.clear();
	}

	public static NativeImage downloadTexture(RenderType renderType, int mipmapLevel) {
		var textureResourceLocation = RenderTypeUtils.getTextureLocation(renderType);

		if (textureResourceLocation == null) {
			return null;
		}

		var image = IMAGE_CACHE.getAndMoveToFirst(textureResourceLocation);

		if (image != null) {
			return image;
		}

		Minecraft
				.getInstance		()
				.getTextureManager	()
				.getTexture			(textureResourceLocation)
				.bind				();

		try (var stack = MemoryStack.stackPush()) {
			var widthBuffer		= stack.callocInt(1);
			var heightBuffer	= stack.callocInt(1);

			glGetTexLevelParameteriv(
					GL_TEXTURE_2D,
					mipmapLevel,
					GL_TEXTURE_WIDTH,
					widthBuffer
			);

			glGetTexLevelParameteriv(
					GL_TEXTURE_2D,
					mipmapLevel,
					GL_TEXTURE_HEIGHT,
					heightBuffer
			);

			var width	= widthBuffer	.get(0);
			var height	= heightBuffer	.get(0);

			if (width == 0 || height == 0) {
				return null;
			}

			var nativeImage = new NativeImage(
					width,
					height,
					false
			);

			nativeImage	.downloadTexture	(mipmapLevel,				false);
			IMAGE_CACHE	.putAndMoveToFirst	(textureResourceLocation,	nativeImage);

			if (IMAGE_CACHE.size() > CoreFeature.getCachedImageSize()) {
				IMAGE_CACHE
						.removeLast	()
						.close		();
			}

			return nativeImage;
		}
	}
}

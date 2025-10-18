package com.github.argon4w.acceleratedrendering.core.utils;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL46.*;

@Mod.EventBusSubscriber(
		modid	= AcceleratedRenderingModEntry	.MOD_ID,
	 	bus		= Mod.EventBusSubscriber.Bus	.MOD,
		value	= Dist							.CLIENT
)
public class TextureUtils implements ResourceManagerReloadListener {

	private	static final TextureUtils													INSTANCE	= new TextureUtils						();
	private	static final Object2ObjectLinkedOpenHashMap<ResourceLocation, NativeImage>	IMAGE_CACHE	= new Object2ObjectLinkedOpenHashMap<>	();

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		IMAGE_CACHE.clear();
	}

	@SubscribeEvent
	public static void onRegisterClientReloadListener(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(INSTANCE);
	}

	public static NativeImage downloadTexture(RenderType renderType, int mipmapLevel) {
		var textureLocation = RenderTypeUtils.getTextureLocation(renderType);

		if (textureLocation == null) {
			return null;
		}

		var image = IMAGE_CACHE.getAndMoveToFirst(textureLocation);

		if (image != null) {
			return image;
		}

		Minecraft
				.getInstance		()
				.getTextureManager	()
				.getTexture			(textureLocation)
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

			nativeImage	.downloadTexture	(mipmapLevel,		false);
			IMAGE_CACHE	.putAndMoveToFirst	(textureLocation,	nativeImage);

			if (IMAGE_CACHE.size() > CoreFeature.getCachedImageSize()) {
				IMAGE_CACHE
						.removeLast	()
						.close		();
			}

			return nativeImage;
		}
	}
}

package com.github.argon4w.acceleratedrendering.core.utils;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public class ShaderUtils {

	public static void setDefaultUniforms(VertexFormat.Mode mode, ShaderInstance shader, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Window window) {
		for(int i = 0; i < 12; ++i) {
			int j = RenderSystem.getShaderTexture(i);
			shader.setSampler("Sampler" + i, j);
		}

		if (shader.MODEL_VIEW_MATRIX != null) {
			shader.MODEL_VIEW_MATRIX.set(modelViewMatrix);
		}

		if (shader.PROJECTION_MATRIX != null) {
			shader.PROJECTION_MATRIX.set(projectionMatrix);
		}

		if (shader.INVERSE_VIEW_ROTATION_MATRIX != null) {
			shader.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
		}

		if (shader.COLOR_MODULATOR != null) {
			shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
		}

		if (shader.GLINT_ALPHA != null) {
			shader.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
		}

		if (shader.FOG_START != null) {
			shader.FOG_START.set(RenderSystem.getShaderFogStart());
		}

		if (shader.FOG_END != null) {
			shader.FOG_END.set(RenderSystem.getShaderFogEnd());
		}

		if (shader.FOG_COLOR != null) {
			shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
		}

		if (shader.FOG_SHAPE != null) {
			shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
		}

		if (shader.TEXTURE_MATRIX != null) {
			shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
		}

		if (shader.GAME_TIME != null) {
			shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
		}

		if (shader.SCREEN_SIZE != null) {
			shader.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
		}

		if (shader.LINE_WIDTH != null && (mode == VertexFormat.Mode.LINES || mode == VertexFormat.Mode.LINE_STRIP)) {
			shader.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
		}

		RenderSystem.setupShaderLights(shader);
	}
}

package com.github.argon4w.acceleratedrendering.core.utils;

import lombok.Getter;
import net.minecraft.util.FastColor;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4i;

@Getter
public class Vertex {

	private final Vector3f position;
	private final Vector2f uv;
	private final Vector4i color;
	private final Vector2i light;
	private final Vector3f normal;

	public Vertex() {
		this.position	= new Vector3f();
		this.uv			= new Vector2f();
		this.color		= new Vector4i();
		this.light		= new Vector2i();
		this.normal		= new Vector3f();
	}

	public int getPackedLight() {
		return light.x | light.y << 16;
	}
}

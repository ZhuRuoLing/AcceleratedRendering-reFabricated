package com.github.argon4w.acceleratedrendering.core.meshes.data;

import com.github.argon4w.acceleratedrendering.core.utils.Vertex;

public interface IMeshData {

	void addVertex(float	posX, float posY, float posZ, float texU, float texV, int colorR, int colorG, int colorB, int colorA, int lightU, int lightV, float normalX, float normalY, float normalZ);
	void addVertex(Vertex	vertex);
}

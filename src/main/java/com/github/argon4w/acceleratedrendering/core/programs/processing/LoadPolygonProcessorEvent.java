package com.github.argon4w.acceleratedrendering.core.programs.processing;

import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.UnaryOperator;

public class LoadPolygonProcessorEvent extends Event implements IModBusEvent {

	private final	VertexFormat		vertexFormat;

	@Getter private IPolygonProcessor	processor;

	public LoadPolygonProcessorEvent(VertexFormat vertexFormat) {
		this.vertexFormat	= vertexFormat;
		this.processor		= EmptyPolygonProcessor.INSTANCE;
	}

	public void loadFor(VertexFormat vertexFormat, UnaryOperator<IPolygonProcessor> selector) {
		if (this.vertexFormat == vertexFormat) {
			this.processor = selector.apply(this.processor);
		}
	}
}

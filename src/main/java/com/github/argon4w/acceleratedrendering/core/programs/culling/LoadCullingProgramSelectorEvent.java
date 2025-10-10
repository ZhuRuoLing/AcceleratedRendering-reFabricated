package com.github.argon4w.acceleratedrendering.core.programs.culling;

import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.UnaryOperator;

public class LoadCullingProgramSelectorEvent extends Event implements IModBusEvent {

	private final	VertexFormat			vertexFormat;

	@Getter private ICullingProgramSelector	selector;

	public LoadCullingProgramSelectorEvent(VertexFormat vertexFormat) {
		this.vertexFormat	= vertexFormat;
		this.selector		= PassThroughCullingProgramSelector.INSTANCE;
	}

	public void loadFor(VertexFormat vertexFormat, UnaryOperator<ICullingProgramSelector> selector) {
		if (this.vertexFormat == vertexFormat) {
			this.selector = selector.apply(this.selector);
		}
	}
}

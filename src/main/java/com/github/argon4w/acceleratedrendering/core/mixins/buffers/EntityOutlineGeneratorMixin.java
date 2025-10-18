package com.github.argon4w.acceleratedrendering.core.mixins.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.AcceleratedEntityOutlineGenerator;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.DecoratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(VertexConsumerExtension					.class)
@Mixin			(OutlineBufferSource.EntityOutlineGenerator	.class)
public class EntityOutlineGeneratorMixin implements IAcceleratedVertexConsumer {

	@Shadow @Final private	VertexConsumer   delegate;

	@Unique private			int              color;

	@Inject(
			method	= "<init>",
			at		= @At("TAIL")
	)
	public void getPackedColor(
			VertexConsumer	delegate,
			int				defaultR,
			int				defaultG,
			int				defaultB,
			int				defaultA,
			CallbackInfo	ci
	) {
		this.color = FastColor.ARGB32.color(
				defaultA,
				defaultR,
				defaultG,
				defaultB
		);
	}

	@Unique
	@Override
	public VertexConsumer decorate(VertexConsumer buffer) {
		return new AcceleratedEntityOutlineGenerator(buffer, color);
	}

	@Unique
	@Override
	public boolean isAccelerated() {
		return delegate
				.getAccelerated	()
				.isAccelerated	();
	}

	@Unique
	@Override
	public <T>  void doRender(
			IAcceleratedRenderer<T>	renderer,
			T						context,
			Matrix4f				transform,
			Matrix3f				normal,
			int						light,
			int						overlay,
			int						color
	) {
		delegate
				.getAccelerated	()
				.doRender		(
						new DecoratedRenderer<>(renderer, this),
						context,
						transform,
						normal,
						light,
						overlay,
						color
				);
	}
}

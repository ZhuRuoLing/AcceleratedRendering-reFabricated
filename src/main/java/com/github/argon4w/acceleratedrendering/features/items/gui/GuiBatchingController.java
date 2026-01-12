package com.github.argon4w.acceleratedrendering.features.items.gui;

import com.github.argon4w.acceleratedrendering.core.CoreFeature;
import com.github.argon4w.acceleratedrendering.core.backends.states.IBindingState;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedGuiGraphics;
import com.github.argon4w.acceleratedrendering.features.items.gui.contexts.DecoratorDrawContext;
import com.github.argon4w.acceleratedrendering.features.items.gui.contexts.HighlightDrawContext;
import com.github.argon4w.acceleratedrendering.features.items.gui.contexts.RectangleDrawContext;
import com.github.argon4w.acceleratedrendering.features.items.gui.contexts.StringDrawContext;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ItemDecoratorHandler;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@SuppressWarnings	("UnstableApiUsage")
@ExtensionMethod	(VertexConsumerExtension.class)
public class GuiBatchingController {

	public static	final GuiBatchingController	INSTANCE = new GuiBatchingController();

	private			final IBindingState					scissorDraw;
	private			final IBindingState					scissorFlush;
	private			final List<StringDrawContext>		stringDrawContexts;
	private			final List<DecoratorDrawContext>	decoratorDrawContexts;
	private			final List<HighlightDrawContext>	highlightDrawContexts;
	private			final List<RectangleDrawContext>	rectangleDrawContexts;

	private GuiBatchingController() {
		this.scissorDraw			= CoreFeature.createScissorState();
		this.scissorFlush			= CoreFeature.createScissorState();
		this.stringDrawContexts		= new ReferenceArrayList<>		();
		this.decoratorDrawContexts	= new ReferenceArrayList<>		();
		this.highlightDrawContexts	= new ReferenceArrayList<>		();
		this.rectangleDrawContexts	= new ReferenceArrayList<>		();
	}

	public void startBatching(GuiGraphics graphics) {
		if (		AcceleratedItemRenderingFeature	.isEnabled						()
				&&	AcceleratedItemRenderingFeature	.shouldUseAcceleratedPipeline	()
				&&	AcceleratedItemRenderingFeature	.shouldAccelerateInGui			()
				&&	AcceleratedItemRenderingFeature	.shouldUseGuiItemBatching		()
				&&	CoreFeature						.isLoaded						()
		) {
			CoreFeature.setGuiBatching	();
			scissorDraw.record			(graphics);
		}
	}

	public void flushBatching(GuiGraphics graphics) {
		if (CoreFeature.isGuiBatching()) {
			var poseStack = graphics.pose();

			CoreFeature.setRenderingGui();

			for (var context : stringDrawContexts) {
				poseStack.pushPose	();
				poseStack.last		().pose		().set(context.transform());
				poseStack.last		().normal	().set(context.normal	());

				graphics.drawString(
						context.font		(),
						context.text		(),
						context.textX		(),
						context.textY		(),
						context.textColor	(),
						context.dropShadow	()
				);

				graphics.pose().popPose();
			}

			for (var context : rectangleDrawContexts) {
				var extension = graphics.bufferSource().getBuffer(context.renderType()).getAccelerated();

				if (extension.isAccelerated()) {
					extension.doRender(
							AcceleratedRectangleRenderer.INSTANCE,
							context,
							context.transform	(),
							context.normal		(),
							context.light		(),
							context.overlay		(),
							context.color		()
					);
				} else {
					poseStack.pushPose	();
					poseStack.last		().pose		().set(context.transform());
					poseStack.last		().normal	().set(context.normal	());

					graphics.fill(
							context.renderType	(),
							context.minX		(),
							context.minY		(),
							context.maxX		(),
							context.maxY		(),
							context.color		()
					);

					graphics.pose().popPose();
				}
			}

			scissorFlush						.record				(graphics);
			scissorDraw							.restore			();
			CoreFeature							.resetGuiBatching	();
			CoreFeature							.resetRenderingGui	();
			((IAcceleratedGuiGraphics) graphics).flushItemBatching	();

			for (var context : decoratorDrawContexts) {
				poseStack.pushPose	();
				poseStack.last		().pose()	.set(context.transform	());
				poseStack.last		().normal()	.set(context.normal		());

				context.handler().render(
						graphics,
						context.font	(),
						context.stack	(),
						context.xOffset	(),
						context.yOffset	()
				);

				graphics.pose().popPose();
			}

			for (var context : highlightDrawContexts) {
				poseStack.pushPose	();
				poseStack.last		().pose		().set(context.transform());
				poseStack.last		().normal	().set(context.normal	());

				AbstractContainerScreen.renderSlotHighlight(
						graphics,
						context.highlightX	(),
						context.highlightY	(),
						context.blitOffset	(),
						context.color		()
				);

				graphics.pose().popPose();
			}

			stringDrawContexts		.clear	();
			decoratorDrawContexts	.clear	();
			highlightDrawContexts	.clear	();
			rectangleDrawContexts	.clear	();
			scissorFlush			.restore();
		}
	}

	public void recordString(
			GuiGraphics	graphics,
			Font		font,
			String		text,
			int			textX,
			int			textY,
			int			textColor,
			boolean		dropShadow
	) {
		var last = graphics
				.pose()
				.last();

		stringDrawContexts.add(new StringDrawContext(
				new Matrix4f(last.pose	()),
				new Matrix3f(last.normal()),
				font,
				text,
				textX,
				textY,
				textColor,
				dropShadow
		));
	}

	public void recordRectangle(
			GuiGraphics	graphics,
			RenderType	renderType,
			int			minX,
			int			minY,
			int			maxX,
			int			maxY,
			int			color
	) {
		var last = graphics
				.pose()
				.last();

		rectangleDrawContexts.add(new RectangleDrawContext(
				new Matrix4f(last.pose	()),
				new Matrix3f(last.normal()),
				renderType,
				minX,
				minY,
				maxX,
				maxY,
				color,
				0,
				0
		));
	}

	public void recordDecorator(
			GuiGraphics				graphics,
			ItemDecoratorHandler	handler,
			Font					font,
			ItemStack				itemStack,
			int						xOffset,
			int						yOffset
	) {
		var last = graphics
				.pose()
				.last();

		decoratorDrawContexts.add(new DecoratorDrawContext(
				new Matrix4f(last.pose	()),
				new Matrix3f(last.normal()),
				handler,
				font,
				itemStack,
				xOffset,
				yOffset
		));
	}

	public void recordHighlight(
			GuiGraphics	graphics,
			int			highlightX,
			int			highlightY,
			int			blitOffset,
			int			color
	) {
		var last = graphics
				.pose()
				.last();

		highlightDrawContexts.add(new HighlightDrawContext(
				new Matrix4f(last.pose	()),
				new Matrix3f(last.normal()),
				highlightX,
				highlightY,
				blitOffset,
				color
		));
	}

	public void delete() {
		scissorDraw	.delete();
		scissorFlush.delete();
	}
}

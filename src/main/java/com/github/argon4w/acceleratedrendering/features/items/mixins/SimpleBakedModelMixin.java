package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.VertexConsumerExtension;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.collectors.CulledMeshCollector;
import com.github.argon4w.acceleratedrendering.core.utils.DirectionUtils;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderContext;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

@ExtensionMethod(VertexConsumerExtension.class)
@Mixin			(SimpleBakedModel		.class)
public abstract class SimpleBakedModelMixin implements IAcceleratedBakedModel, IAcceleratedRenderer<AcceleratedItemRenderContext> {

	@Shadow public abstract List<BakedQuad> getQuads(BlockState pState, Direction pDirection, RandomSource pRandom);

	@Unique private final Map<IBufferGraph, Int2ObjectMap<IMesh>> meshes = new Object2ObjectOpenHashMap<>();

	@Unique
	@Override
	public void renderItemFast(
			ItemStack					itemStack,
			PoseStack					poseStack,
			IAcceleratedVertexConsumer	extension,
			int							combinedLight,
			int							combinedOverlay
	) {
		PoseStack.Pose pose = poseStack.last();

		extension.doRender(
				this,
				new AcceleratedItemRenderContext(
						itemStack,
						null,
						null
				),
				pose.pose(),
				pose.normal(),
				combinedLight,
				combinedOverlay,
				-1
		);
	}

	@Unique
	@Override
	public void render(
			VertexConsumer					vertexConsumer,
			AcceleratedItemRenderContext	context,
			Matrix4f						transform,
			Matrix3f						normal,
			int								light,
			int								overlay,
			int								color
	) {
		var itemStack	= context		.getItemStack	();
		var itemColor	= context		.getItemColor	();
		var extension	= vertexConsumer.getAccelerated	();
		var layers		= meshes		.get			(extension);

		extension.beginTransform(transform, normal);

		if (layers != null) {
			for (int layer : layers.keySet()) {
				var mesh = layers.get(layer);

				mesh.write(
						extension,
						getCustomColor(layer, itemColor.getColor(itemStack, layer)),
						light,
						overlay
				);
			}

			extension.endTransform();
			return;
		}

		var culledMeshCollectors	= new Int2ObjectOpenHashMap<CulledMeshCollector>();
		layers 						= new Int2ObjectLinkedOpenHashMap<>				();
		meshes.put																	(extension, layers);

		for (var direction : DirectionUtils.FULL) {
			for (var quad : getQuads(null, direction, null)) {
				var culledMeshCollector = culledMeshCollectors.get(quad.getTintIndex());

				if (culledMeshCollector == null) {
					culledMeshCollector = new CulledMeshCollector	(extension);
					culledMeshCollectors.put						(quad.getTintIndex(), culledMeshCollector);
				}

				var meshBuilder = extension	.decorate	(culledMeshCollector);
				var data		= quad		.getVertices();

				for (int i = 0; i < data.length / 8; i++) {
					var vertexOffset	= i * IQuadTransformer.STRIDE;
					var posOffset		= vertexOffset + IQuadTransformer.POSITION;
					var colorOffset		= vertexOffset + IQuadTransformer.COLOR;
					var uv0Offset		= vertexOffset + IQuadTransformer.UV0;
					var uv2Offset		= vertexOffset + IQuadTransformer.UV2;
					var normalOffset	= vertexOffset + IQuadTransformer.NORMAL;
					var packedNormal	= data[normalOffset];

                    float normalX = ((byte) (packedNormal & 0xFF)) / 127.0f;
                    float normalY = ((byte) ((packedNormal >> 8) & 0xFF)) / 127.0f;
                    float normalZ = ((byte) ((packedNormal >> 16) & 0xFF)) / 127.0f;

                    if (normalX == 0 && normalY == 0 && normalZ == 0) {
                        normalX = quad.getDirection().getNormal().getX();
                        normalY = quad.getDirection().getNormal().getY();
                        normalZ = quad.getDirection().getNormal().getZ();
                    }

					meshBuilder.addVertex(
							Float.intBitsToFloat(data[posOffset + 0]),
							Float.intBitsToFloat(data[posOffset + 1]),
							Float.intBitsToFloat(data[posOffset + 2]),
							data[colorOffset],
							Float.intBitsToFloat(data[uv0Offset + 0]),
							Float.intBitsToFloat(data[uv0Offset + 1]),
							-1,
							data[uv2Offset],
                        	normalX,
                        	normalY,
                        	normalZ
					);
				}
			}
		}

		for (int layer : culledMeshCollectors.keySet()) {
			var culledMeshCollector = culledMeshCollectors.get(layer);
			culledMeshCollector.flush();

			var mesh = AcceleratedItemRenderingFeature
					.getMeshType()
					.getBuilder	()
					.build		(culledMeshCollector);

			layers	.put	(layer, mesh);
			mesh	.write	(
					extension,
					getCustomColor(layer, itemColor.getColor(itemStack, layer)),
					light,
					overlay
			);
		}

		extension.endTransform();
	}


	@Unique
	@Override
	public boolean isAccelerated() {
		return true;
	}

	@Unique
	@Override
	public boolean isAcceleratedInHand() {
		return false;
	}

	@Unique
	@Override
	public boolean isAcceleratedInGui() {
		return false;
	}

	@Unique
	@Override
	public int getCustomColor(int layer, int color) {
		return layer == -1 ? -1 : color;
	}
}

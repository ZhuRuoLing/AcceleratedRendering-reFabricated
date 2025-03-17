package com.github.argon4w.acceleratedrendering.features.items.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.MeshCollector;
import com.github.argon4w.acceleratedrendering.core.mixins.MCItemColorsAccessor;
import com.github.argon4w.acceleratedrendering.core.utils.CullerUtils;
import com.github.argon4w.acceleratedrendering.core.utils.TextureUtils;
import com.github.argon4w.acceleratedrendering.features.items.AcceleratedItemRenderingFeature;
import com.github.argon4w.acceleratedrendering.features.items.EmptyItemColor;
import com.github.argon4w.acceleratedrendering.features.items.IAcceleratedBakedModel;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(SimpleBakedModel.class)
public class SimpleBakedModelMixin implements IAcceleratedBakedModel {

    @Shadow @Final protected List<BakedQuad> unculledFaces;
    @Shadow @Final protected Map<Direction, List<BakedQuad>> culledFaces;

    @Unique private final Map<RenderType, Int2ObjectMap<IMesh>> meshes = new Object2ObjectOpenHashMap<>();;

    @Override
    public void renderItemFast(ItemStack itemStack, PoseStack poseStack, IAcceleratedVertexConsumer extension, int combinedLight, int combinedOverlay) {
        PoseStack.Pose pose = poseStack.last();
        ItemColor itemColor = getItemColorOrDefault(itemStack);

        extension.beginTransform(pose.pose(), pose.normal());

        for (RenderType renderType : extension.getRenderTypes()) {
            Int2ObjectMap<IMesh> layers = meshes.get(renderType);

            if (layers != null) {
                for (int layer : layers.keySet()) {
                    IMesh mesh = layers.get(layer);

                    mesh.write(
                            extension,
                            itemColor.getColor(itemStack, layer),
                            combinedLight,
                            combinedOverlay
                    );
                }

                continue;
            }

            layers = new Int2ObjectLinkedOpenHashMap<>();
            meshes.put(renderType, layers);

            IMesh.Builder builder = AcceleratedItemRenderingFeature.getMeshBuilder();
            Int2ObjectMap<MeshCollector> meshCollectors = new Int2ObjectLinkedOpenHashMap<>();
            NativeImage image = TextureUtils.downloadTexture(renderType, 0);
            List<BakedQuad> allFaces = new ArrayList<>(unculledFaces);

            for (Direction direction : culledFaces.keySet()) {
                allFaces.addAll(culledFaces.get(direction));
            }

            for (BakedQuad quad : allFaces) {
                int[] vertices = quad.getVertices();
                int layer = quad.getTintIndex();
                int size = vertices.length / 8;

                MeshCollector meshCollector = meshCollectors.get(layer);

                if (meshCollector == null) {
                    meshCollector = builder.newMeshCollector(renderType);
                    meshCollectors.put(layer, meshCollector);
                }

                ModelPart.Vertex[] modelVertices = new ModelPart.Vertex[size];
                int[] modelColors = new int[size];
                Vector3f[] modelNormals = new Vector3f[size];

                for (int i = 0; i < size; i++) {
                    int vertexOffset = i * IQuadTransformer.STRIDE;
                    int posOffset = vertexOffset + IQuadTransformer.POSITION;
                    int colorOffset = vertexOffset + IQuadTransformer.COLOR;
                    int uv0Offset = vertexOffset + IQuadTransformer.UV0;
                    int normalOffset = vertexOffset + IQuadTransformer.NORMAL;

                    float posX = Float.intBitsToFloat(vertices[posOffset + 0]);
                    float posY = Float.intBitsToFloat(vertices[posOffset + 1]);
                    float posZ = Float.intBitsToFloat(vertices[posOffset + 2]);

                    int packedColor = vertices[colorOffset];

                    float u0 = Float.intBitsToFloat(vertices[uv0Offset + 0]);
                    float v0 = Float.intBitsToFloat(vertices[uv0Offset + 1]);

                    int packedNormal = vertices[normalOffset];
                    float normalX = ((byte) (packedNormal & 0xFF)) / 127.0f;
                    float normalY = ((byte) ((packedNormal >> 8) & 0xFF)) / 127.0f;
                    float normalZ = ((byte) ((packedNormal >> 16) & 0xFF)) / 127.0f;

                    modelVertices[i] = new ModelPart.Vertex(posX, posY, posZ, u0, v0);
                    modelColors[i] = packedColor;
                    modelNormals[i] = new Vector3f(normalX, normalY, normalZ);
                }

                if (!CullerUtils.shouldCull(modelVertices, image)) {
                    for (int i = 0; i < size; i++) {
                        ModelPart.Vertex vertex = modelVertices[i];
                        int packedColor = modelColors[i];
                        Vector3f normal = modelNormals[i];

                        meshCollector.addVertex(
                                vertex.pos.x,
                                vertex.pos.y,
                                vertex.pos.z,
                                packedColor,
                                vertex.u,
                                vertex.v,
                                combinedOverlay,
                                combinedLight,
                                normal.x,
                                normal.y,
                                normal.z
                        );
                    }
                }
            }

            for (int layer : meshCollectors.keySet()) {
                MeshCollector meshCollector = meshCollectors.get(layer);
                IMesh mesh = builder.build(meshCollector);

                layers.put(layer, mesh);
                mesh.write(
                        extension,
                        itemColor.getColor(itemStack, layer),
                        combinedLight,
                        combinedOverlay
                );
            }
        }

        extension.endTransform();
    }

    @Override
    public boolean isAccelerated() {
        return true;
    }

    @Override
    public boolean hasCustomColor() {
        return false;
    }

    @Override
    public int getCustomColor() {
        return 0;
    }

    private static ItemColor getItemColorOrDefault(ItemStack itemStack){
        MCItemColorsAccessor accessor = (MCItemColorsAccessor) Minecraft.getInstance();
        int id = BuiltInRegistries.ITEM.getId(itemStack.getItem());
        ItemColor color = ((ItemColorsAccessor) (accessor.getItemColors())).getItemColors().byId(id);
        if (color == null)return EmptyItemColor.INSTANCE;
        return color;
    }
}

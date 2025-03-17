package com.github.argon4w.acceleratedrendering.core.programs;

import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeShader;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class ComputeShaderProgramLoader extends SimplePreparableReloadListener<Map<ResourceLocation, ComputeShaderProgramLoader.ShaderSource>> {

    private static final Map<ResourceLocation, ComputeProgram> COMPUTE_SHADERS = new Object2ObjectOpenHashMap<>();
    public static final ComputeShaderProgramLoader INSTANCE = new ComputeShaderProgramLoader();

    @Override
    protected Map<ResourceLocation, ComputeShaderProgramLoader.ShaderSource> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        try {
            ImmutableMap.Builder<ResourceLocation, ShaderDefinition> builder = ImmutableMap.builder();
            ModLoader.postEvent(new LoadComputeShaderEvent(builder));

            Map<ResourceLocation, ShaderSource> shaderSources = new Object2ObjectOpenHashMap<>();
            Map<ResourceLocation, ShaderDefinition> shaderLocations = builder.build();

            for (ResourceLocation key : shaderLocations.keySet()) {
                ShaderDefinition definition = shaderLocations.get(key);
                ResourceLocation resourceLocation = definition.location;
                int barrierFlags = definition.barrierFlags;

                if (resourceLocation == null) {
                    throw new IllegalStateException("Found empty shader location on: \"" + key + "\"");
                }

                Optional<Resource> resource = resourceManager.getResource(resourceLocation);

                if (resource.isEmpty()) {
                    throw new IllegalStateException("Cannot found compute shader: \"" + resourceLocation + "\"");
                }

                try (InputStream stream = resource.get().open()) {
                    shaderSources.put(key, new ShaderSource(new String(stream.readAllBytes(), StandardCharsets.UTF_8), barrierFlags));
                }
            }

            return shaderSources;
        } catch (Exception e) {
            throw new ReportedException(CrashReport.forThrowable(e, "Exception while loading compute shader"));
        }
    }

    @Override
    protected void apply(
            Map<ResourceLocation, ShaderSource> shaderSources,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        RenderSystem.recordRenderCall(() -> {
            for (ResourceLocation key : shaderSources.keySet()) {
                try {
                    ShaderSource source = shaderSources.get(key);
                    String shaderSource = source.source;
                    int barrierFlags = source.barrierFlags;

                    ComputeProgram program = new ComputeProgram(barrierFlags);
                    ComputeShader computeShader = new ComputeShader();

                    computeShader.setShaderSource(shaderSource);

                    if (!computeShader.compileShader()) {
                        throw new IllegalStateException("Shader \"" + key + "\" failed to compile because of the following errors: " + computeShader.getInfoLog());
                    }

                    program.attachShader(computeShader);

                    if (!program.linkProgram()) {
                        throw new IllegalStateException("Program \"" + key + "\" failed to link because of the following errors: " + program.getInfoLog());
                    }

                    computeShader.delete();
                    COMPUTE_SHADERS.put(key, program);
                } catch (Exception e) {
                    throw new ReportedException(CrashReport.forThrowable(e, "Exception while compiling/linking compute shader"));
                }
            }
        });
    }

    public static ComputeProgram getProgram(ResourceLocation resourceLocation) {
        ComputeProgram program = COMPUTE_SHADERS.get(resourceLocation);

        if (program == null) {
            throw new IllegalStateException("Get shader program \""+ resourceLocation + "\" too early! Program is not loaded yet!");
        }

        return program;
    }

    public record ShaderDefinition(ResourceLocation location, int barrierFlags) {

    }

    public record ShaderSource(String source, int barrierFlags) {

    }
}

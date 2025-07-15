package com.maaackz.weirdcraft.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Accessor("builtChunks")
    ObjectArrayList<ChunkBuilder.BuiltChunk> getBuiltChunks();
    @Accessor("cameraChunkX")
    void setCameraChunkX(int x);
    @Accessor("cameraChunkY")
    void setCameraChunkY(int y);
    @Accessor("cameraChunkZ")
    void setCameraChunkZ(int z);
    @Accessor("cameraChunkX")
    int getCameraChunkX();
    @Accessor("cameraChunkY")
    int getCameraChunkY();
    @Accessor("cameraChunkZ")
    int getCameraChunkZ();
    @Accessor("chunks")
    BuiltChunkStorage getChunks();
    @Accessor("chunkBuilder")
    ChunkBuilder getChunkBuilder();
    @Accessor("world")
    net.minecraft.client.world.ClientWorld getWorld();
} 
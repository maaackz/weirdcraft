package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingChunkManager;
import com.maaackz.weirdcraft.DreamcastingClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldRenderer.class)
public class DreamcastingVisibleSectionsMixin {
    @Inject(method = "updateChunks", at = @At("TAIL"))
    private void onUpdateChunkPositions(CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting()) {
            List<ChunkBuilder.BuiltChunk> visibleChunks =
                ((WorldRendererAccessor) (Object) this).getBuiltChunks();
            for (ChunkBuilder.BuiltChunk chunk : DreamcastingChunkManager.getRenderChunks()) {
                if (!visibleChunks.contains(chunk)) {
                    visibleChunks.add(chunk);
                }
            }
            // Also inject into builtChunks for vanilla renderer
            it.unimi.dsi.fastutil.objects.ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks =
                ((WorldRendererAccessor) (Object) this).getBuiltChunks();
            WorldRenderer worldRenderer = (WorldRenderer)(Object)this;
            ChunkBuilder chunkBuilder = ((WorldRendererAccessor) (Object) this).getChunkBuilder();
            for (ChunkBuilder.BuiltChunk chunk : DreamcastingChunkManager.getRenderChunks()) {
                if (!builtChunks.contains(chunk)) {
                    builtChunks.add(chunk);
                    System.out.println("[Dreamcasting] Added BuiltChunk at " + chunk.getOrigin() + ", isEmpty=" + chunk.getData().isEmpty());
                    // Attempt to force a rebuild of the chunk mesh
                    if (chunkBuilder != null) {
                        chunk.scheduleRebuild(chunkBuilder, new ChunkRendererRegionBuilder());
                        System.out.println("[Dreamcasting] Forced rebuild for chunk at " + chunk.getOrigin());
                    } else {
                        System.out.println("[Dreamcasting] ChunkBuilder is null, cannot force rebuild for chunk at " + chunk.getOrigin());
                    }
                    // Advanced debug: print chunk data status
                    try {
                        // Get the world from WorldRenderer
                        net.minecraft.client.world.ClientWorld world = ((com.maaackz.weirdcraft.mixin.client.WorldRendererAccessor) worldRenderer).getWorld();
                        net.minecraft.util.math.BlockPos origin = chunk.getOrigin();
                        net.minecraft.util.math.ChunkPos chunkPos = new net.minecraft.util.math.ChunkPos(origin);
                        net.minecraft.world.chunk.WorldChunk worldChunk = null;
                        if (world != null) {
                            worldChunk = world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, net.minecraft.world.chunk.ChunkStatus.FULL, false);
                        }
                        String status = worldChunk != null ? worldChunk.getStatus().toString() : "null";
                        int nonEmptySections = -1;
                        if (worldChunk != null && worldChunk.getSectionArray() != null) {
                            nonEmptySections = (int) java.util.Arrays.stream(worldChunk.getSectionArray()).filter(s -> s != null && !s.isEmpty()).count();
                        }
                        System.out.println("[Dreamcasting] BuiltChunk at " + chunk.getOrigin() +
                            ", isEmpty=" + chunk.getData().isEmpty() +
                            ", status=" + status +
                            ", nonEmptySections=" + nonEmptySections);
                    } catch (Exception e) {
                        System.out.println("[Dreamcasting] Error printing advanced chunk debug: " + e);
                    }
                }
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting()) {
            List<ChunkBuilder.BuiltChunk> visibleChunks = ((WorldRendererAccessor) (Object) this).getBuiltChunks();
            System.out.println("[Dreamcasting] [HEAD] visibleChunks size: " + visibleChunks.size());
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting()) {
            List<ChunkBuilder.BuiltChunk> visibleChunks = ((WorldRendererAccessor) (Object) this).getBuiltChunks();
            for (ChunkBuilder.BuiltChunk chunk : DreamcastingChunkManager.getRenderChunks()) {
                if (!visibleChunks.contains(chunk)) {
                    visibleChunks.add(chunk);
                    System.out.println("[Dreamcasting] [TAIL] Added BuiltChunk at " + chunk.getOrigin() + ", isEmpty=" + chunk.getData().isEmpty());
                }
            }
            System.out.println("[Dreamcasting] [TAIL] visibleChunks size: " + visibleChunks.size());
        }
    }
} 
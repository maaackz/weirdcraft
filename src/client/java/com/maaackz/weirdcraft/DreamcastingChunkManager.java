package com.maaackz.weirdcraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class DreamcastingChunkManager {
    
    private static final ConcurrentMap<Long, WorldChunk> dreamcastingChunks = new ConcurrentHashMap<>();
    private static boolean isActive = false;
    private static final AtomicLong lastLogTime = new AtomicLong(0);
    private static final long LOG_INTERVAL_MS = 1000; // Only log once per second
    
    public static void setActive(boolean active) {
        isActive = active;
        if (!active) {
            // Clear all dreamcasting chunks when deactivating
            int chunkCount = dreamcastingChunks.size();
            dreamcastingChunks.clear();
            System.out.println("Dreamcasting chunk manager deactivated, cleared " + chunkCount + " chunks");
            
            // Force garbage collection to help clear memory
            System.gc();
        } else {
            System.out.println("Dreamcasting chunk manager activated");
        }
    }
    
    public static boolean isActive() {
        return isActive;
    }
    
    public static void storeChunk(WorldChunk chunk) {
        if (!isActive) return;
        
        long chunkPos = chunk.getPos().toLong();
        boolean wasNew = dreamcastingChunks.put(chunkPos, chunk) == null;
        if (wasNew) {
            long now = System.currentTimeMillis();
            long last = lastLogTime.get();
            if (now - last > LOG_INTERVAL_MS) {
                lastLogTime.set(now);
                System.out.println("Stored new dreamcasting chunk at " + chunk.getPos() + " (total: " + dreamcastingChunks.size() + ")");
            }
        }
    }
    
    public static WorldChunk getChunk(int x, int z) {
        if (!isActive) return null;
        
        long chunkPos = ChunkPos.toLong(x, z);
        return dreamcastingChunks.get(chunkPos);
    }
    
    public static boolean hasChunk(int x, int z) {
        if (!isActive) return false;
        
        long chunkPos = ChunkPos.toLong(x, z);
        return dreamcastingChunks.containsKey(chunkPos);
    }
    
    public static void removeChunk(int x, int z) {
        if (!isActive) return;
        
        long chunkPos = ChunkPos.toLong(x, z);
        WorldChunk removed = dreamcastingChunks.remove(chunkPos);
        if (removed != null) {
            System.out.println("Removed dreamcasting chunk at " + x + ", " + z);
        }
    }
    
    public static void clearAllChunks() {
        dreamcastingChunks.clear();
        System.out.println("Cleared all dreamcasting chunks");
    }
    
    public static void forceCleanup() {
        isActive = false;
        dreamcastingChunks.clear();
        System.out.println("Forced cleanup of dreamcasting chunk manager");
        System.gc();
    }
    
    public static int getChunkCount() {
        return dreamcastingChunks.size();
    }
    
    public static boolean hasChunkSilent(int x, int z) {
        if (!isActive) return false;
        long chunkPos = ChunkPos.toLong(x, z);
        return dreamcastingChunks.containsKey(chunkPos);
    }
    
    public static void debugChunks() {
        System.out.println("=== DREAMCASTING CHUNKS DEBUG ===");
        System.out.println("Active: " + isActive);
        System.out.println("Chunk count: " + dreamcastingChunks.size());
        for (Long chunkPos : dreamcastingChunks.keySet()) {
            int x = ChunkPos.getPackedX(chunkPos);
            int z = ChunkPos.getPackedZ(chunkPos);
            System.out.println("  Chunk at " + x + ", " + z);
        }
        System.out.println("=== END DEBUG ===");
    }
    
    public static boolean hasChunkAtPosition(int x, int z) {
        if (!isActive) return false;
        long chunkPos = ChunkPos.toLong(x, z);
        return dreamcastingChunks.containsKey(chunkPos);
    }
    
    public static void forceChunkIntoWorld(net.minecraft.world.World world, int x, int z) {
        if (!isActive) return;
        
        WorldChunk chunk = getChunk(x, z);
        if (chunk != null && world != null) {
            try {
                // Try to force the chunk to be recognized by the world
                world.getChunkManager().getChunk(x, z, ChunkStatus.FULL, true);
            } catch (Exception e) {
                System.out.println("Could not force chunk into world: " + e.getMessage());
            }
        }
    }

    public static List<ChunkBuilder.BuiltChunk> getRenderChunks() {
        List<ChunkBuilder.BuiltChunk> result = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.worldRenderer == null) return result;

        // Get all BuiltChunks from the renderer
        // This assumes you can access all built chunks (may need an accessor if not public)
        List<ChunkBuilder.BuiltChunk> allBuiltChunks = new ArrayList<>();
        // TODO: If you have a way to get all built chunks, use it here. For now, try to get from visibleChunks
        try {
            allBuiltChunks.addAll(((com.maaackz.weirdcraft.mixin.client.WorldRendererAccessor) client.worldRenderer).getBuiltChunks());
        } catch (Exception e) {
            // If you have a better way to get all built chunks, use it here
        }

        for (WorldChunk chunk : dreamcastingChunks.values()) {
            ChunkPos pos = chunk.getPos();
            for (ChunkBuilder.BuiltChunk builtChunk : allBuiltChunks) {
                if (new ChunkPos(builtChunk.getOrigin()).equals(pos)) {
                    result.add(builtChunk);
                    break;
                }
            }
        }
        return result;
    }

    public static java.util.List<int[]> getAllChunkCoords() {
        return new java.util.ArrayList<>();
    }
} 
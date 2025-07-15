package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingChunkManager;
import com.maaackz.weirdcraft.DreamcastingClient;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin {

    @Shadow
    protected abstract WorldChunk getChunk(int x, int z, ChunkStatus status, boolean load);

    //    @Accessor("chunks")
    //    public abstract Long2ObjectMap<WorldChunk> getChunks();

    @Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/WorldChunk;", at = @At("RETURN"), cancellable = true)
    private void onGetChunk(int x, int z, ChunkStatus status, boolean load, CallbackInfoReturnable<WorldChunk> cir) {
        // Only provide chunks if dreamcasting is actively enabled
        if (DreamcastingChunkManager.isActive() && cir.getReturnValue() == null) {
            WorldChunk chunk = DreamcastingChunkManager.getChunk(x, z);
            if (chunk != null) {
                System.out.println("Dreamcasting: Providing chunk from custom manager at " + x + ", " + z);
                cir.setReturnValue(chunk);
            }
        }
    }

    @Inject(method = "loadChunkFromPacket", at = @At("RETURN"))
    private void onLoadChunkFromPacket(int x, int z, net.minecraft.network.PacketByteBuf buf, 
                                     net.minecraft.nbt.NbtCompound heightmaps, 
                                     java.util.function.Consumer<net.minecraft.network.packet.s2c.play.ChunkData.BlockEntityVisitor> consumer, 
                                     CallbackInfoReturnable<WorldChunk> cir) {
        // Only store chunks when dreamcasting is actively enabled
        if (DreamcastingChunkManager.isActive()) {
            WorldChunk chunk = cir.getReturnValue();
            if (chunk != null) {
                DreamcastingChunkManager.storeChunk(chunk);
                
                // Try to force entity loading if the chunk has entities
                try {
                    // Force the chunk to load entities if it has any
                    if (chunk.getStatus().isAtLeast(net.minecraft.world.chunk.ChunkStatus.FULL)) {
                        chunk.loadEntities();
                    }
                } catch (Exception e) {
                    // Ignore any errors during entity loading
                    System.out.println("Note: Could not force entity loading for chunk at " + x + ", " + z);
                }
            }
        }
    }
} 
package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.network.EntityResponsePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ChunkDataSender;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class DreamcastingServer {

    public static void handleEntityRequest(ServerPlayerEntity player) {

        // Find the requested entity by ID
        Entity entity = getRandomEntity(player.getServerWorld(), player);

        if (entity != null) {
            // Load the chunks around the entity
            loadChunksAroundEntity(entity, player);
            ServerChunkManager chunkManager = player.getServerWorld().getChunkManager();
//            chunkManager.loadEntity(entity);
            // Send the entity information to the client
            EntityResponsePayload responsePayload = new EntityResponsePayload(
                    entity.getId(),
                    entity.getBlockPos(),
                    entity.getName().getString()
            );

            ServerPlayNetworking.send(player, responsePayload);
            System.out.println("Entity response sent.");
        }
    }

    private static Entity getRandomEntity(ServerWorld world, ServerPlayerEntity player) {
        if (world != null) {
            // Increase the search radius to beyond the normal render distance
            int range = 10000; // Increase this value to search a larger area
            Box searchArea = new Box(player.getPos().add(-range, -range, -range), player.getPos().add(range, range, range));

            // Get all entities within the larger search radius
            List<Entity> entities = world.getEntitiesByClass(Entity.class, searchArea, e -> e != player);

            // Filter entities to only include those with the name tag "test"
            entities.removeIf(entity -> entity.getCustomName() == null || !entity.getCustomName().getString().equals("test"));

            if (!entities.isEmpty()) {
                // Pick a random entity with the name tag "test"
                Random random = new Random();
                return entities.get(random.nextInt(entities.size()));
            } else {
                return null;
            }
        }
        return null;
    }

    public static void loadChunksAroundEntity(Entity entity, ServerPlayerEntity player) {

        int range = 4; // The range around the entity to load chunks (adjust as necessary)
        BlockPos pos = entity.getBlockPos();

        ServerWorld serverWorld = (ServerWorld) entity.getWorld();
        ServerChunkManager chunkManager = serverWorld.getChunkManager();
        ChunkManager playerChunkManager = player.getWorld().getChunkManager();
        ServerPlayNetworkHandler serverPlayNetworkHandler = player.networkHandler;
        ChunkDataSender chunkDataSender = serverPlayNetworkHandler.chunkDataSender;

        // Loop through the surrounding area to load the chunks
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                BlockPos chunkPos = pos.add(x * 16, 0, z * 16); // Only need to load chunks in the horizontal plane

                // Load the chunk (force load it)
                WorldChunk chunk = (WorldChunk) chunkManager.getChunk(chunkPos.getX() >> 4, chunkPos.getZ() >> 4, ChunkStatus.FULL, true);


//                chunk.loadEntities();
//                chunk.loadFromPacket(packet.getChunkData().getSectionsDataBuf(), new NbtCompound(),
//                        packet.getChunkData().getBlockEntities(packet.getChunkX(), packet.getChunkZ()));
//                chunkManager.loadEntity(entity);
//                System.out.println(entity.isRegionUnloaded());

                assert chunk != null;
                chunkManager.addTicket(ChunkTicketType.START, chunk.getPos(), 8, Unit.INSTANCE);
                chunkDataSender.add(chunk);

//                chunkManager.
//                assert chunk != null;
                assert chunk != null;
                serverPlayNetworkHandler.sendPacket(new ChunkDataS2CPacket(chunk, entity.getWorld().getLightingProvider(), (BitSet)null, (BitSet)null));
                // After the chunk is loaded, send the chunk data to the player
//                sendChunkToPlayer(chunk, player);

            }
        }

        chunkDataSender.sendChunkBatches(player);

    }

    private static void sendChunkToPlayer(WorldChunk chunk, ServerPlayerEntity player, ServerWorld serverWorld) {
        // Send the chunk to the player

    }
}

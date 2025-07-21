package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.network.DreamcastEntitySyncPayload;
import com.maaackz.weirdcraft.network.EntityResponsePayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DreamcastingServer {

    // Track which players are in dreamcasting mode and which entities they're spectating
    private static final ConcurrentMap<ServerPlayerEntity, Entity> spectatingPlayers = new ConcurrentHashMap<>();

    static {
        // Register a server tick event to sync entity state to spectating players
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var entry : spectatingPlayers.entrySet()) {
                ServerPlayerEntity player = entry.getKey();
                Entity entity = entry.getValue();
                if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                    // Stop spectating if entity is gone
                    spectatingPlayers.remove(player);
                    continue;
                }
                // Build the sync payload
                Identifier entityTypeId = Registries.ENTITY_TYPE.getId(entity.getType());
                int entityId = entity.getId();
                BlockPos pos = entity.getBlockPos();
                float yaw = entity.getHeadYaw();
                float pitch = entity.getPitch();
                Vec3d velocity = entity.getVelocity();
                NbtCompound nbt = new NbtCompound();
                entity.writeNbt(nbt);
                // Patch: Remove sleeping state if entity is not sleeping (robust villager check)
                boolean isSleeping = false;
                if (entity instanceof net.minecraft.entity.LivingEntity living) {
                    // Default: use isSleeping() for non-villagers
                    try {
                        isSleeping = living.isSleeping();
                    } catch (Exception ignored) {}
                    // For villagers, use Brain.memories last_slept/last_woken
                    if (entity.getType().getTranslationKey().contains("villager") && nbt.contains("Brain")) {
                        net.minecraft.nbt.NbtCompound brain = nbt.getCompound("Brain");
                        if (brain.contains("memories")) {
                            net.minecraft.nbt.NbtCompound memories = brain.getCompound("memories");
                            if (memories.contains("minecraft:last_slept") && memories.contains("minecraft:last_woken")) {
                                long lastSlept = memories.getCompound("minecraft:last_slept").getLong("value");
                                long lastWoken = memories.getCompound("minecraft:last_woken").getLong("value");
                                isSleeping = lastSlept > lastWoken;
                            }
                        }
                    }
                    if (!isSleeping) {
                        nbt.remove("Sleeping");
                        nbt.remove("sleeping"); // Some mappings use lowercase
                        nbt.remove("SleepingX");
                        nbt.remove("SleepingY");
                        nbt.remove("SleepingZ");
                    }
                }
                // Debug: Print the full NBT being sent
                // System.out.println("[DreamcastServer Debug] Sending NBT to client: " + nbt);
                DreamcastEntitySyncPayload payload = new DreamcastEntitySyncPayload(
                    entityTypeId, entityId, pos, velocity, yaw, pitch, nbt
                );
                ServerPlayNetworking.send(player, payload);
            }
        });
    }

    public static void handleEntityRequest(ServerPlayerEntity player) {
        // First, look for a non-self player entity
        ServerWorld world = player.getServerWorld();
        Entity entity = null;
        for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
            if (!otherPlayer.getUuid().equals(player.getUuid())) {
                entity = otherPlayer;
                break;
            }
        }
        // If no other player found, look for a 'test'-named entity
        if (entity == null) {
            entity = findRandomEntityWithNameTag(world, player, "test");
        }

        if (entity != null) {
            System.out.println("Found entity: " + entity.getName().getString() + " at " + entity.getBlockPos());
            
            // Calculate distance to entity
            double distance = player.getPos().distanceTo(entity.getPos());
            System.out.println("Distance to entity: " + distance + " blocks");
            
            // Load chunks around the entity and send them to the player
            loadAndSendChunksAroundEntity(entity, player);
            
            // Track that this player is spectating this entity
            spectatingPlayers.put(player, entity);
            
            // Send the entity information to the client
            EntityResponsePayload responsePayload = new EntityResponsePayload(
                    entity.getId(),
                    entity.getBlockPos(),
                    entity.getName().getString()
            );

            ServerPlayNetworking.send(player, responsePayload);
            System.out.println("Entity response sent for entity ID: " + entity.getId());
        } else {
            System.out.println("No entity with name tag 'test' found.");
        }
    }

    private static Entity findRandomEntityWithNameTag(ServerWorld world, ServerPlayerEntity player, String nameTag) {
        if (world == null) return null;
        
        // Search in a very large radius to find entities
        int searchRadius = 10000;
        Box searchArea = new Box(
            player.getPos().add(-searchRadius, -searchRadius, -searchRadius),
            player.getPos().add(searchRadius, searchRadius, searchRadius)
        );

        System.out.println("Searching for entities with name tag '" + nameTag + "' in area: " + searchArea);

        // Get all entities in the search area
        List<Entity> entities = world.getEntitiesByClass(Entity.class, searchArea, e -> 
            e != player && 
            e.getCustomName() != null && 
            e.getCustomName().getString().equals(nameTag)
        );

        System.out.println("Found " + entities.size() + " entities with name tag '" + nameTag + "'");

            if (!entities.isEmpty()) {
            Random random = new Random();
            Entity selectedEntity = entities.get(random.nextInt(entities.size()));
            System.out.println("Selected entity: " + selectedEntity.getName().getString() + " at " + selectedEntity.getBlockPos());
            return selectedEntity;
        }
        
        // If no entities found in the current world, try other worlds
        System.out.println("No entities found in current world, checking other worlds...");
        
        // Try to find entities in other worlds (like the nether or end)
        for (ServerWorld otherWorld : world.getServer().getWorlds()) {
            if (otherWorld != world) {
                System.out.println("Searching in world: " + otherWorld.getRegistryKey().getValue());
                
                // For other worlds, we'll search around the origin or use a different strategy
                Box otherWorldSearchArea = new Box(
                    new BlockPos(-searchRadius, -searchRadius, -searchRadius).toCenterPos(),
                    new BlockPos(searchRadius, searchRadius, searchRadius).toCenterPos()
                );
                
                List<Entity> otherWorldEntities = otherWorld.getEntitiesByClass(Entity.class, otherWorldSearchArea, e -> 
                    e.getCustomName() != null && 
                    e.getCustomName().getString().equals(nameTag)
                );
                
                if (!otherWorldEntities.isEmpty()) {
                Random random = new Random();
                    Entity selectedEntity = otherWorldEntities.get(random.nextInt(otherWorldEntities.size()));
                    System.out.println("Found entity in other world: " + selectedEntity.getName().getString() + " at " + selectedEntity.getBlockPos());
                    return selectedEntity;
            }
        }
        }
        
        System.out.println("No entities with name tag '" + nameTag + "' found in any world");
        return null;
    }

    public static void loadAndSendChunksAroundEntity(Entity entity, ServerPlayerEntity player) {
        ServerWorld serverWorld = (ServerWorld) entity.getWorld();
        ServerChunkManager chunkManager = serverWorld.getChunkManager();
        ServerPlayNetworkHandler networkHandler = player.networkHandler;
        
        // Get the entity's chunk position
        ChunkPos entityChunkPos = new ChunkPos(entity.getBlockPos());
        
        // Load chunks in a radius around the entity
        int chunkRadius = 3; // Load 3 chunks in each direction for better coverage
        
        System.out.println("Loading chunks around entity at chunk: " + entityChunkPos);
        
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                ChunkPos chunkPos = new ChunkPos(entityChunkPos.x + x, entityChunkPos.z + z);

                // Force load the chunk to FULL status with entities
                WorldChunk chunk = (WorldChunk) chunkManager.getChunk(
                    chunkPos.x, 
                    chunkPos.z, 
                    ChunkStatus.FULL, 
                    true
                );

                // Ensure the chunk is fully loaded with entities
                if (chunk != null && chunk.getStatus().isAtLeast(ChunkStatus.FULL)) {
                    // Force entity loading if needed
                    chunk.loadEntities();
                }
                
                if (chunk != null) {
                    // Add a ticket to keep the chunk loaded
                    chunkManager.addTicket(ChunkTicketType.START, chunkPos, 8, Unit.INSTANCE);
                    
                    // Send the chunk data to the player
                    try {
                        ChunkDataS2CPacket chunkPacket = new ChunkDataS2CPacket(
                            chunk, 
                            serverWorld.getLightingProvider(), 
                            null, 
                            null
                        );
                        networkHandler.sendPacket(chunkPacket);
                        System.out.println("Sent chunk data for chunk: " + chunkPos);
                    } catch (Exception e) {
                        System.out.println("Error sending chunk data: " + e.getMessage());
                    }
                } else {
                    System.out.println("Failed to load chunk: " + chunkPos);
                }
            }
        }
        
        // Wait a bit for chunks to be processed, then ensure the entity is loaded
        serverWorld.getServer().execute(() -> {
            // Double-check that the entity is still valid and loaded
            if (entity.isAlive() && !entity.isRemoved()) {
                System.out.println("Entity confirmed loaded: " + entity.getId() + " at " + entity.getBlockPos());
                
                // Also ensure the entity's chunk is properly loaded for the player
                ChunkPos entityChunk = new ChunkPos(entity.getBlockPos());
                if (chunkManager.isChunkLoaded(entityChunk.x, entityChunk.z)) {
                    System.out.println("Entity's chunk is confirmed loaded for player");
                    
                    // Ensure the entity is properly tracked for the player
                    try {
                        // Force the entity to be tracked by the player's chunk manager
                        // This should make the entity accessible to the client
                        WorldChunk chunk = chunkManager.getWorldChunk(entityChunk.x, entityChunk.z);
                        chunk.setNeedsSaving(true);

                        // Also ensure the entity is properly loaded in the chunk
                        if (chunk.getStatus().isAtLeast(ChunkStatus.FULL)) {
                            System.out.println("Entity chunk is fully loaded and entity " + entity.getId() + " should be accessible");
                        } else {
                            System.out.println("Warning: Entity chunk is not fully loaded, status: " + chunk.getStatus());
                        }
                    } catch (Exception e) {
                        System.out.println("Error ensuring entity tracking: " + e.getMessage());
                    }
                } else {
                    System.out.println("Warning: Entity's chunk is not loaded for player");
                }
            } else {
                System.out.println("Entity is no longer valid after chunk loading");
            }
        });
    }

    // Method to stop spectating for a player
    public static void stopSpectating(ServerPlayerEntity player) {
        spectatingPlayers.remove(player);
        System.out.println("Player " + player.getName().getString() + " stopped spectating");
    }

    // Method to get the entity a player is currently spectating
    public static Entity getSpectatedEntity(ServerPlayerEntity player) {
        return spectatingPlayers.get(player);
    }

    // Debug method to test chunk loading
    public static void debugChunkLoading(ServerPlayerEntity player, BlockPos pos) {
        ServerWorld serverWorld = player.getServerWorld();
        ServerChunkManager chunkManager = serverWorld.getChunkManager();
        ChunkPos chunkPos = new ChunkPos(pos);
        
        System.out.println("=== CHUNK LOADING DEBUG ===");
        System.out.println("Testing chunk loading at: " + chunkPos);
        System.out.println("Player position: " + player.getBlockPos());
        System.out.println("Target position: " + pos);
        
        // Check if chunk is already loaded
        boolean isLoaded = chunkManager.isChunkLoaded(chunkPos.x, chunkPos.z);
        System.out.println("Chunk already loaded: " + isLoaded);
        
        // Try to load the chunk
        WorldChunk chunk = (WorldChunk) chunkManager.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
        if (chunk != null) {
            System.out.println("Successfully loaded chunk: " + chunkPos);
            chunkManager.addTicket(ChunkTicketType.START, chunkPos, 8, Unit.INSTANCE);
        } else {
            System.out.println("Failed to load chunk: " + chunkPos);
        }
        
        System.out.println("=== END DEBUG ===");
    }
}

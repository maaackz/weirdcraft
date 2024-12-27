package com.maaackz.weirdcraft.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    public void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
//        ServerWorld world = (ServerWorld) (Object) this;
//        ServerChunkManager chunkManager = world.getChunkManager();
//
//        // Get a list of all entities in the world within the specified range
//        List<Entity> entities = world.getEntitiesByClass(Entity.class, new Box(50000, 50000, 50000, 50000, 50000, 50000), Objects::nonNull);
//
//        // Iterate through all entities
//        for (Entity entity : entities) {
//            if (entity.getCustomName() != null && entity.getCustomName().getString().equals("test")) {
//                System.out.println("LOAD CHUNK @ " + entity.getBlockPos());
//                // Entity has the name tag "test", keep the chunk containing this entity loaded
//
//                ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());
//
//                // Force load the chunk containing the "test" entity by adding a ticket
//                chunkManager.addTicket(ChunkTicketType.START, chunkPos, 8, Unit.INSTANCE);
//                chunkManager.loadEntity(entity);
//                // Optionally, send the chunk data to all players
//                sendChunkToPlayers(world, chunkPos);
//            }
//        }
//    }
//
//    // Method to send chunk data to all players
//    @Unique
//    private void sendChunkToPlayers(ServerWorld world, ChunkPos chunkPos) {
//        // Iterate through all players in the world
//        for (ServerPlayerEntity player : world.getPlayers()) {
//            // Send the chunk data to each player
//            WorldChunk chunk = (WorldChunk) world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
//            if (chunk != null) {
//                // Send the chunk data packet to the player
//                player.networkHandler.sendPacket(new ChunkDataS2CPacket(chunk, world.getLightingProvider(), null, null));
//
//            }
//        }
//    }
}

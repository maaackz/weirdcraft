package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.network.EntityRequestPayload;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import com.maaackz.weirdcraft.CustomSpectateCamera;

import java.util.List;
import java.util.Random;

public class DreamcastingClient {

    private static boolean isDreamcasting = false; // Track if the client is in dreamcasting mode
    private static Screen previousScreen = null; // Store the previous screen before switching
    private static boolean wasSleeping = false; // Track if the player was sleeping before entering dreamcasting mode

    // Method to toggle Dreamcasting mode and spectate a random entity
    public static void dreamcast(MinecraftClient client, boolean enable) {
        isDreamcasting = enable;
        
        // Activate/deactivate the dreamcasting chunk manager
        DreamcastingChunkManager.setActive(enable);
        
        if (isDreamcasting) {
        requestEntityFromServer();
        } else {
            // Clean up dreamcasting state
            cleanupDreamcasting(client);
        }
        
        // Preserve the original screen before changing the camera
        if (client.currentScreen != null) {
            previousScreen = client.currentScreen;
        }

        // Handle the player sleep state
        if (client.player != null && client.player.isSleeping()) {
            wasSleeping = true;
        }

        // Make sure that leaving bed works correctly by checking if player is in bed
        if (client.player != null && client.player.isSleeping() && !wasSleeping) {
            client.setScreenAndRender(new SleepingChatScreen());
        } else if (previousScreen != null) {
            // If there's a previous screen, go back to it after toggling dreamcasting
            client.setScreenAndRender(previousScreen);
        }

        // Reset wasSleeping flag after processing
        wasSleeping = false; // Reset flag after handling dreamcasting logic
    }
    
    // Method to clean up dreamcasting state
    private static void cleanupDreamcasting(MinecraftClient client) {
        // Return camera to player if it's not already
        if (client.cameraEntity != client.player && client.player != null) {
            client.setCameraEntity(client.player);
        }
        
        // Reset perspective
        resetFirstPersonPerspective(client);
        
        // Force the client to stop waiting for chunks by clearing any pending requests
        if (client.world != null && client.world.getChunkManager() instanceof ClientChunkManager) {
            ClientChunkManager chunkManager = (ClientChunkManager) client.world.getChunkManager();
            // Force a chunk manager update to clear any pending operations
            try {
                // This will force the chunk manager to process any pending operations
                chunkManager.tick(() -> true, true);
                
                // Force multiple ticks to ensure all pending operations are processed
                for (int i = 0; i < 5; i++) {
                    chunkManager.tick(() -> true, true);
                }
            } catch (Exception e) {
                System.out.println("Error during chunk manager cleanup: " + e.getMessage());
            }
        }
        
        // Clear any custom camera entities that might still be in the world
        if (client.world != null) {
            client.world.getEntitiesByClass(CustomSpectateCamera.class, 
                new Box(client.player.getPos().add(-1000, -1000, -1000), 
                       client.player.getPos().add(1000, 1000, 1000)), 
                entity -> true).forEach(Entity::discard);
        }
        
        // Force a world tick to process any remaining operations
        if (client.world != null) {
            try {
                client.world.tick(() -> true);
            } catch (Exception e) {
                System.out.println("Error during world tick cleanup: " + e.getMessage());
            }
        }
        
        // Force cleanup of the chunk manager
        DreamcastingChunkManager.forceCleanup();
        
        // Force the client to stop waiting for chunks by clearing any pending chunk requests
        // This should help with the "Waiting for chunk..." message in F3
        if (client.world != null) {
            try {
                // Force the client to process any remaining chunk operations
                client.world.getChunkManager().tick(() -> true, true);
                
                // Clear any pending chunk loading requests
                for (int x = -32; x <= 32; x++) {
                    for (int z = -32; z <= 32; z++) {
                        // Force the client to stop waiting for chunks in a large area
                        client.world.getChunkManager().getChunk(x, z, ChunkStatus.EMPTY, false);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error during chunk request cleanup: " + e.getMessage());
            }
        }
        
        System.out.println("Dreamcasting cleanup completed");
    }

    public static void requestEntityFromServer() {
        // Create the payload with the entity ID
        EntityRequestPayload payload = new EntityRequestPayload(1);

        // Send the payload to the server
        ClientPlayNetworking.send(payload);
        System.out.println("Entity request sent.");
    }

    // Method to make the client spectate a random entity
    private static void spectateRandomEntity(MinecraftClient client) {
        World world = client.world;
        if (world != null) {
            assert client.player != null;
            setThirdPersonPerspective(client);
            // Increase the search radius to beyond the normal render distance
            int range = 10000; // Increase this value to search a larger area
            Box searchArea = new Box(client.player.getPos().add(-range, -range, -range), client.player.getPos().add(range, range, range));

            // Get all entities within the larger search radius
            List<Entity> entities = world.getEntitiesByClass(Entity.class, searchArea, e -> e != client.player);

            // Filter entities to only include those with the name tag "test"
            entities.removeIf(entity -> entity.getCustomName() == null || !entity.getCustomName().getString().equals("test"));

            if (!entities.isEmpty()) {
                // Pick a random entity with the name tag "test" to spectate
                Random random = new Random();
                Entity randomEntity = entities.get(random.nextInt(entities.size()));

                // Set the camera to spectate the selected entity
                client.setCameraEntity(randomEntity);
            }
        }
    }

    // Method to make the client spectate a specific entity
    public static void spectateEntity(MinecraftClient client, Entity entity) {
        if (entity == null) {
            System.out.println("Cannot spectate null entity");
            return;
        }
        
        World world = client.world;
        if (world != null) {
            assert client.player != null;
            
            // Ensure the chunk containing the entity is loaded
            ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());
            
            // Try to get the chunk from our custom manager first
            WorldChunk chunk = DreamcastingChunkManager.getChunk(chunkPos.x, chunkPos.z);
            if (chunk == null) {
                // If not in our manager, try to load it normally
                world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
            }
            
            // Set third-person perspective for better spectating
            setThirdPersonPerspective(client);
            
            // Set the camera to spectate the entity
            client.setCameraEntity(entity);
            System.out.println("Now spectating entity: " + entity.getName().getString() + " (ID: " + entity.getId() + ")");
        }
    }

    // Method to spectate an entity at a specific position (for dreamcasting)
    public static void spectateEntityAtPosition(MinecraftClient client, BlockPos entityPos, String entityName) {
        World world = client.world;
        if (world == null) {
            System.out.println("Cannot spectate entity: world is null");
            return;
        }
        
        assert client.player != null;
        
        // Ensure the chunk containing the entity is loaded
        ChunkPos chunkPos = new ChunkPos(entityPos);
        
        // Try to get the chunk from our custom manager first
        WorldChunk chunk = DreamcastingChunkManager.getChunk(chunkPos.x, chunkPos.z);
        if (chunk == null) {
            System.out.println("Cannot spectate entity: chunk not found in dreamcasting manager");
            return;
        }
        
        // Set third-person perspective for better spectating
        setThirdPersonPerspective(client);
        
        // Create a custom camera entity that follows the entity position
        CustomSpectateCamera camera = new CustomSpectateCamera(world, entityPos, entityName);
        
        // Add the camera entity to the world if not already present
        if (!world.getEntitiesByClass(CustomSpectateCamera.class, camera.getBoundingBox(), e -> true).contains(camera)) {
            world.spawnEntity(camera);
        }
        
        // Set the camera to our custom spectate camera
        client.setCameraEntity(camera);
        System.out.println("Now spectating entity at position: " + entityPos + " (Name: " + entityName + ")");
    }

    // Method to stop spectating and return the camera to the player
    public static void stopSpectating(MinecraftClient client) {
        if (client.player != null) {
            // Return the camera to the player immediately
            client.setCameraEntity(client.player);
            
            resetFirstPersonPerspective(client);
            
            // Send packet to stop sleeping (this wakes the player up)
            ClientPlayNetworkHandler clientPlayNetworkHandler = client.player.networkHandler;
            clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));

            // Wait for a moment to allow the game to process the wake-up
            client.execute(() -> {
                // Ensure the camera is still on the player
                client.setCameraEntity(client.player);
                System.out.println("Camera returned to player after waking up.");
            });

            // Optional: Check if the player is still sleeping and debug
            System.out.println("Sleeping? " + client.player.isSleeping());
        }
    }

    // Force third-person perspective (F5)
    private static void setThirdPersonPerspective(MinecraftClient client) {
        // Change the perspective to third-person (F5)
        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    // Reset to first-person perspective
    private static void resetFirstPersonPerspective(MinecraftClient client) {
        // Change the perspective back to first-person
        client.options.setPerspective(Perspective.FIRST_PERSON);
    }

    // Force the camera entity and perspective during dreamcasting to prevent flicker
    public static void forceCameraLock(MinecraftClient client) {
        if (!isDreamcasting) return;
        // Find the custom camera entity if it exists
        if (client.cameraEntity == null || !(client.cameraEntity instanceof CustomSpectateCamera)) {
            // Try to find the custom camera in the world
            if (client.world != null) {
                for (Entity entity : client.world.getEntities()) {
                    if (entity instanceof CustomSpectateCamera) {
                        client.setCameraEntity(entity);
                        break;
                    }
                }
            }
        }
        // Optionally, force third-person perspective
        if (client.options.getPerspective() != Perspective.THIRD_PERSON_BACK) {
            client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
    }

    // Optionally, you could log when Dreamcasting is toggled
    public static void logDreamcastingToggle() {
        LogUtils.getLogger().info("Dreamcasting toggled: " + (isDreamcasting ? "Enabled" : "Disabled"));
    }

    // Get the current state of Dreamcasting (for debugging or other purposes)
    public static boolean isDreamcasting() {
        return isDreamcasting;
    }

    // Get the current dreamcasting camera entity, if any
    public static Entity getDreamcastingCameraEntity() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.cameraEntity instanceof CustomSpectateCamera) {
            return client.cameraEntity;
        }
        return null;
    }
}

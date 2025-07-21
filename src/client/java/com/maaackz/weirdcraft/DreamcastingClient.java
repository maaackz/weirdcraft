package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.network.EntityRequestPayload;
import com.maaackz.weirdcraft.network.RequestChunkReloadPayload;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.List;
import java.util.Random;

public class DreamcastingClient {

    private static boolean isDreamcasting = false; // Track if the client is in dreamcasting mode
    private static long lastDreamcastEndTime = 0; // Track when dreamcasting ended
    private static Screen previousScreen = null; // Store the previous screen before switching
    private static boolean wasSleeping = false; // Track if the player was sleeping before entering dreamcasting mode

    // Method to toggle Dreamcasting mode and spectate a random entity
    public static void dreamcast(MinecraftClient client, boolean enable) {
        System.out.println("[Dreamcasting] dreamcast called with enable=" + enable);
        isDreamcasting = enable;
        System.out.println("[DEBUG] dreamcast() called with enable=" + enable);
        if (enable) {
            // Run chunk manager replacement before entering dreamcasting
            replaceClientChunkManager();
        }
        if (!enable) {
            lastDreamcastEndTime = System.currentTimeMillis();
        }
        
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
        
        // Force camera rotation reset after sleep state changes
//        if (enable && client.player != null && client.player.isSleeping()) {
//            client.execute(() -> {
//                // Force a camera update to ensure rotation is applied after sleep
//                if (client.cameraEntity != null) {
////                    client.gameRenderer.close();
////                            (client.cameraEntity.getHeadYaw());
////                    client.cameraEntity.setPitch(client.cameraEntity.getPitch());
//                    System.out.println("[Dreamcasting] Forced camera rotation reset after sleep");
//                }
//            });
//        }
    }
    
    // Method to clean up dreamcasting state
    private static void cleanupDreamcasting(MinecraftClient client) {
        client.execute(() -> {
            System.out.println("[Dreamcasting] cleanupDreamcasting called");
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

            // --- Clear all loaded client chunks and force a renderer reload ---
            if (client.world != null && client.world.getChunkManager() != null) {
                Object chunkManager = client.world.getChunkManager();
                try {
                    // Access the 'chunks' field (ClientChunkMap)
                    java.lang.reflect.Field chunksField = chunkManager.getClass().getDeclaredField("chunks");
                    chunksField.setAccessible(true);
                    Object chunkMap = chunksField.get(chunkManager);

                    System.out.println("[Dreamcasting] About to print all ClientChunkMap fields");
                    for (java.lang.reflect.Field field : chunkMap.getClass().getDeclaredFields()) {
                        System.out.println("[Dreamcasting] ClientChunkMap field: " + field.getName() + " type: " + field.getType().getName());
                    }
                    System.out.println("[Dreamcasting] About to print all ClientChunkManager fields");
                    for (java.lang.reflect.Field field : chunkManager.getClass().getDeclaredFields()) {
                        System.out.println("[Dreamcasting] ClientChunkManager field: " + field.getName() + " type: " + field.getType().getName());
                    }

                    // --- Aggressively clear all Map, Set, and List fields in ClientChunkMap ---
                    System.out.println("[Dreamcasting] Clearing all Map/Set/List fields in ClientChunkMap");
                    for (java.lang.reflect.Field field : chunkMap.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(chunkMap);
                        if (value instanceof java.util.Map) {
                            ((java.util.Map<?, ?>) value).clear();
                            System.out.println("[Dreamcasting] Cleared Map field in ClientChunkMap: " + field.getName());
                        } else if (value instanceof java.util.Set) {
                            ((java.util.Set<?>) value).clear();
                            System.out.println("[Dreamcasting] Cleared Set field in ClientChunkMap: " + field.getName());
                        } else if (value instanceof java.util.List) {
                            ((java.util.List<?>) value).clear();
                            System.out.println("[Dreamcasting] Cleared List field in ClientChunkMap: " + field.getName());
                        }
                    }

                    // --- Aggressively clear all Map, Set, and List fields in ClientChunkManager ---
                    System.out.println("[Dreamcasting] Clearing all Map/Set/List fields in ClientChunkManager");
                    for (java.lang.reflect.Field field : chunkManager.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(chunkManager);
                        if (value instanceof java.util.Map) {
                            ((java.util.Map<?, ?>) value).clear();
                            System.out.println("[Dreamcasting] Cleared Map field in ClientChunkManager: " + field.getName());
                        } else if (value instanceof java.util.Set) {
                            ((java.util.Set<?>) value).clear();
                            System.out.println("[Dreamcasting] Cleared Set field in ClientChunkManager: " + field.getName());
                        } else if (value instanceof java.util.List) {
                            ((java.util.List<?>) value).clear();
                            System.out.println("[Dreamcasting] Cleared List field in ClientChunkManager: " + field.getName());
                        }
                    }

                    // --- Debug: Print ClientChunkMap contents after cleanup ---
                    for (java.lang.reflect.Field field : chunkMap.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(chunkMap);
                        if (value instanceof java.util.Map) {
                            System.out.println("[Dreamcasting] ClientChunkMap Map field after cleanup: " + field.getName() + " size=" + ((java.util.Map<?, ?>) value).size());
                        } else if (value instanceof java.util.Set) {
                            System.out.println("[Dreamcasting] ClientChunkMap Set field after cleanup: " + field.getName() + " size=" + ((java.util.Set<?>) value).size());
                        } else if (value instanceof java.util.List) {
                            System.out.println("[Dreamcasting] ClientChunkMap List field after cleanup: " + field.getName() + " size=" + ((java.util.List<?>) value).size());
                        }
                    }
                    for (java.lang.reflect.Field field : chunkManager.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(chunkManager);
                        if (value instanceof java.util.Map) {
                            System.out.println("[Dreamcasting] ClientChunkManager Map field after cleanup: " + field.getName() + " size=" + ((java.util.Map<?, ?>) value).size());
                        } else if (value instanceof java.util.Set) {
                            System.out.println("[Dreamcasting] ClientChunkManager Set field after cleanup: " + field.getName() + " size=" + ((java.util.Set<?>) value).size());
                        } else if (value instanceof java.util.List) {
                            System.out.println("[Dreamcasting] ClientChunkManager List field after cleanup: " + field.getName() + " size=" + ((java.util.List<?>) value).size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("[Dreamcasting] Exception during aggressive chunk cache clear: " + e);
                }
                client.worldRenderer.reload();
            }

            // --- Explicitly request chunks around the player to force reload ---
            if (client.player != null && client.world != null) {
                int playerChunkX = client.player.getBlockPos().getX() >> 4;
                int playerChunkZ = client.player.getBlockPos().getZ() >> 4;
                int radius = 5; // 11x11 area

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        client.world.getChunkManager().getChunk(playerChunkX + dx, playerChunkZ + dz, net.minecraft.world.chunk.ChunkStatus.FULL, true);
                    }
                }
                // Nudge the player's position to force a chunk request
                client.player.setPosition(client.player.getX(), client.player.getY(), client.player.getZ());
                System.out.println("[Dreamcasting] Forced chunk reload around player after dreamcasting.");

                // --- Debug: Print chunk statuses around the player ---
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        net.minecraft.world.chunk.WorldChunk chunk = client.world.getChunkManager().getChunk(playerChunkX + dx, playerChunkZ + dz, net.minecraft.world.chunk.ChunkStatus.FULL, false);
                        System.out.println("[Dreamcasting] Chunk at " + (playerChunkX + dx) + "," + (playerChunkZ + dz) + ": " + (chunk == null ? "null" : chunk.getStatus()));
                    }
                }

                // --- Test: Teleport player 100 blocks away and back ---
                double origX = client.player.getX();
                double origY = client.player.getY();
                double origZ = client.player.getZ();
                client.player.setPosition(origX + 100, origY, origZ);
                System.out.println("[Dreamcasting] Teleported player 100 blocks away for chunk reload test.");
                // Wait a tick, then teleport back (schedule on client thread)
                client.execute(() -> {
                    client.player.setPosition(origX, origY, origZ);
                    System.out.println("[Dreamcasting] Teleported player back to original position after chunk reload test.");
                });
            }

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
        });
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
            // setThirdPersonPerspective(client);
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
                Camera camera = client.gameRenderer.getCamera();
                client.gameRenderer.reset();
                camera.reset();
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
        if (!isDreamcasting) {
            System.out.println("[DEBUG] Ignoring spectateEntity because not dreamcasting.");
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
            
            // Set first-person perspective for immersive spectating
            resetFirstPersonPerspective(client);
            
            // Set the camera to spectate the entity
            client.setCameraEntity(entity);
            Camera camera = client.gameRenderer.getCamera();
            System.out.println("[ROTATION PRINT] " + camera.getRotation() + " : " + camera.getYaw() + " : " + camera.getPitch());
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
        
        // Try to find the dreamcasted entity and spectate it directly
        Entity dreamcastedEntity = com.maaackz.weirdcraft.WeirdcraftClient.getDreamcastedEntity();
        if (dreamcastedEntity != null) {
            // Set first-person perspective for immersive spectating
            resetFirstPersonPerspective(client);
            
            // Set the camera to spectate the dreamcasted entity directly
            client.setCameraEntity(dreamcastedEntity);
            System.out.println("Now spectating dreamcasted entity: " + dreamcastedEntity.getName().getString() + " at position: " + entityPos);
        } else {
            System.out.println("Cannot spectate entity: no dreamcasted entity available");
        }
    }

    // Method to stop spectating and return the camera to the player
    public static void stopSpectating(MinecraftClient client) {
        client.execute(() -> {
            System.out.println("[Dreamcasting] stopSpectating called");
            isDreamcasting = false;
            System.out.println("[DEBUG] isDreamcasting set to false in stopSpectating()");
            if (client.player != null) {
                // Return the camera to the player immediately
                client.setCameraEntity(client.player);

                resetFirstPersonPerspective(client);

                // Send packet to stop sleeping (this wakes the player up)
                ClientPlayNetworkHandler clientPlayNetworkHandler = client.player.networkHandler;
                clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));

                // --- Always send chunk reload packet after camera is restored ---
                System.out.println("[Dreamcasting] Sending RequestChunkReloadPayload to server after spectate end");
                ClientPlayNetworking.send(new RequestChunkReloadPayload(5)); // 5 = radius (11x11 area)

                // --- Force local chunk reload and renderer reload ---
                if (client.world != null && client.worldRenderer != null) {
                    client.worldRenderer.reload();
                    System.out.println("[Dreamcasting] Called worldRenderer.reload() after chunk reload packet");
                }
            }
        });
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
//        if (!isDreamcasting) return;
        
        // Ensure we're spectating the dreamcasted entity
//        Entity dreamcastedEntity = com.maaackz.weirdcraft.WeirdcraftClient.getDreamcastedEntity();
//        if (dreamcastedEntity != null && client.cameraEntity != dreamcastedEntity) {
//            client.setCameraEntity(dreamcastedEntity);
//            System.out.println("[CameraLock] Forced camera to dreamcasted entity: " + dreamcastedEntity.getName().getString());
//        }
        
        // Optionally, force third-person perspective
        // if (client.options.getPerspective() != Perspective.THIRD_PERSON_BACK) {
        //     client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        // }
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
        // Use the static fakeCameraPlayerEntity from WeirdcraftClient
        try {
            Class<?> clientClass = Class.forName("com.maaackz.weirdcraft.WeirdcraftClient");
            java.lang.reflect.Field field = clientClass.getDeclaredField("fakeCameraPlayerEntity");
            field.setAccessible(true);
            Object fakeCamera = field.get(null);
            if (fakeCamera instanceof Entity && ((Entity)fakeCamera).isAlive()) {
                return (Entity)fakeCamera;
            }
        } catch (Exception e) {
            // Ignore errors
        }
        return null;
    }

    // Failsafe: On each client tick, if not dreamcasting and camera is not player, and it's soon after dreamcast ended, force camera back
    public static void clientTickFailsafe(MinecraftClient client) {
        if (!isDreamcasting && client.player != null && client.cameraEntity != client.player) {
            System.out.println("[Failsafe] Camera forcibly returned to player after dreamcasting. Camera entity was: " + (client.cameraEntity != null ? client.cameraEntity.getName().getString() : "null"));
            client.setCameraEntity(client.player);
            resetFirstPersonPerspective(client);
        }
    }

    // Manual test method for chunk clear/reload
    public static void manualChunkClearTest() {
        System.out.println("[Dreamcasting] manualChunkClearTest called");
        cleanupDreamcasting(MinecraftClient.getInstance());
        replaceClientChunkManager();
    }

    // Replace the ClientChunkManager instance on the world with a new one
    public static void replaceClientChunkManager() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            System.out.println("[Dreamcasting] replaceClientChunkManager: client.world is null");
            return;
        }
        try {
            System.out.println("[Dreamcasting] Attempting to replace ClientChunkManager...");
            java.lang.reflect.Field chunkManagerField = client.world.getClass().getDeclaredField("chunkManager");
            chunkManagerField.setAccessible(true);
            Object oldChunkManager = chunkManagerField.get(client.world);
            Class<?> chunkManagerClass = oldChunkManager.getClass();
            java.lang.reflect.Constructor<?> ctor = chunkManagerClass.getDeclaredConstructors()[0];
            ctor.setAccessible(true);
            // Try to match the constructor signature (ClientWorld, int)
            Object newChunkManager = null;
            for (java.lang.reflect.Constructor<?> c : chunkManagerClass.getDeclaredConstructors()) {
                Class<?>[] params = c.getParameterTypes();
                if (params.length == 2 && params[0].isAssignableFrom(client.world.getClass()) && params[1] == int.class) {
                    c.setAccessible(true);
                    newChunkManager = c.newInstance(client.world, 32); // 32 is a typical view distance
                    break;
                }
            }
            if (newChunkManager == null) {
                System.out.println("[Dreamcasting] Could not find suitable ClientChunkManager constructor");
                return;
            }
            chunkManagerField.set(client.world, newChunkManager);
            System.out.println("[Dreamcasting] Successfully replaced ClientChunkManager instance!");
            // Request chunk reload from server for area around player
            ClientPlayNetworking.send(new RequestChunkReloadPayload(5)); // 5 = radius (11x11 area)
            System.out.println("[Dreamcasting] Sent RequestChunkReloadPayload to server");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Dreamcasting] Exception while replacing ClientChunkManager: " + e);
        }
    }
}

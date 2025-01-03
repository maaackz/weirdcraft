package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.network.EntityRequestPayload;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class DreamcastingClient {

    private static boolean isDreamcasting = false; // Track if the client is in dreamcasting mode
    private static Screen previousScreen = null; // Store the previous screen before switching
    private static boolean wasSleeping = false; // Track if the player was sleeping before entering dreamcasting mode

    // Method to toggle Dreamcasting mode and spectate a random entity
    public static void dreamcast(MinecraftClient client, boolean enable) {
        isDreamcasting = enable;
        requestEntityFromServer();
        // Preserve the original screen before changing the camera
        if (client.currentScreen != null) {
            previousScreen = client.currentScreen;
        }

        // Handle the player sleep state
        if (client.player != null && client.player.isSleeping()) {
            wasSleeping = true;
        }

        if (isDreamcasting) {
//            spectateRandomEntity(client);  // Start spectating a random entity
//            setThirdPersonPerspective(client);  // Force third-person perspective (F5)
        } else {
//            stopSpectating(client);  // Stop spectating, return to normal view
//            resetFirstPersonPerspective(client);  // Reset back to first-person view
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

    // Method to make the client spectate a random entity
    public static void spectateEntity(MinecraftClient client, Entity entity) {
        World world = client.world;
        if (world != null) {
            assert client.player != null;
            setThirdPersonPerspective(client);
            client.setCameraEntity(entity);

        }
    }


    // Method to stop spectating and return the camera to the player
    public static void stopSpectating(MinecraftClient client) {
        if (client.player != null) {
            resetFirstPersonPerspective(client);
            // Send packet to stop sleeping (this wakes the player up)
            ClientPlayNetworkHandler clientPlayNetworkHandler = client.player.networkHandler;
            clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));

            // Wait for a moment to allow the game to process the wake-up
            // We will explicitly set the camera to the player after the wake-up
            client.execute(() -> {
                // Return the camera to the player after waking up
                client.setCameraEntity(client.player);  // Return the camera to the player
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

    // Optionally, you could log when Dreamcasting is toggled
    public static void logDreamcastingToggle() {
        LogUtils.getLogger().info("Dreamcasting toggled: " + (isDreamcasting ? "Enabled" : "Disabled"));
    }

    // Get the current state of Dreamcasting (for debugging or other purposes)
    public static boolean isDreamcasting() {
        return isDreamcasting;
    }

}

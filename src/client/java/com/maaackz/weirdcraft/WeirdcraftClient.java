package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.datagen.CustomEntities;
import com.maaackz.weirdcraft.item.custom.PocketWatchItem;
import com.maaackz.weirdcraft.item.custom.RainesCloudItem;
import com.maaackz.weirdcraft.network.*;
import com.maaackz.weirdcraft.renderer.HolyMackerelRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import org.lwjgl.glfw.GLFW;

public class WeirdcraftClient implements ClientModInitializer {

	private static final KeyBinding specialOne = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.weirdcraft.special_one",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_Z, // Default key: Z
			"category.weirdcraft.keys"
	));
	private static final KeyBinding specialTwo = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.weirdcraft.special_two",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_X, // Default key: X
			"category.weirdcraft.keys"
	));
	private static final KeyBinding specialThree = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.weirdcraft.special_three",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_C, // Default key: X
			"category.weirdcraft.keys"
	));

	boolean timeAdvancing;
	boolean timeReversing;

    // Store the current dreamcasted entity for updates and camera
    private static Entity dreamcastedEntity = null;


	@Override
	public void onInitializeClient() {
		System.out.println("Client initialized.");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		if (FabricLoader.getInstance().isModLoaded("modmenu")) {
			// Register your custom logic for Mod Menu here
			registerModMenuSound();
		}

		EntityRendererRegistry.register(CustomEntities.HOLY_MACKEREL, HolyMackerelRenderer::new);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.world == null) return;

			boolean holdingPocketWatch = client.player.getMainHandStack().getItem() instanceof PocketWatchItem;

			if (holdingPocketWatch) {
				if (specialOne.isPressed()) {
					if (!timeAdvancing) { // Start advancing
						timeAdvancing = true;
						timeReversing = false; // Stop reverse if active
						ClientPlayNetworking.send(new TimePayload(true, true));
						System.out.println("Started advancing time...");
					}
				} else if (timeAdvancing) { // Stop advancing
					timeAdvancing = false;
					ClientPlayNetworking.send(new TimePayload(true, false));
					System.out.println("Stopped advancing time...");
				}

				if (specialTwo.isPressed()) {
					if (!timeReversing) { // Start reversing
						timeReversing = true;
						timeAdvancing = false; // Stop advance if active
						ClientPlayNetworking.send(new TimePayload(false, true));
						System.out.println("Started reversing time...");
					}
				} else if (timeReversing) { // Stop reversing
					timeReversing = false;
					ClientPlayNetworking.send(new TimePayload(false, false));
					System.out.println("Stopped reversing time...");
				}
			}


			boolean holdingRainCloud = client.player.getMainHandStack().getItem() instanceof RainesCloudItem;
			if (holdingRainCloud) {
				if (specialOne.isPressed()) {
					ClientPlayNetworking.send(new WeatherPayload(1));
					System.out.println("Weather cleared.");

				}
				else if (specialTwo.isPressed()) {
					ClientPlayNetworking.send(new WeatherPayload(2));
					System.out.println("Rain.");

				}
				else if (specialThree.isPressed()) {
					ClientPlayNetworking.send(new WeatherPayload(3));
					System.out.println("Thunder.");

				}
			}

		});
// NOTE: PayloadTypeRegistry has 2 functions:
//       - playS2C is for server -> client communication
//       - playC2S is for client -> server communication

// In your common initializer method


// Client-side handler to process the SleepPayload
		ClientPlayNetworking.registerGlobalReceiver(SleepPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				boolean isSleeping = payload.bool();

				// Trigger Dreamcasting logic based on whether the player is sleeping
				if (isSleeping) {
					// Handle the start of the sleep (Dreamcasting)
					DreamcastingClient.dreamcast(context.client(), true);  // You may pass additional data as needed
				} else {
					// Handle the stop of the sleep
					DreamcastingClient.dreamcast(context.client(), false);  // Similarly, handle stop logic
				}
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(EntityResponsePayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				System.out.println("Entity response received for entity ID: " + payload.entityId());
				System.out.println("Entity position: " + payload.entityPos());
				System.out.println("Entity name: " + payload.entityName());

				World world = context.client().world;
				if (world == null) {
					System.out.println("World is null, cannot process entity response");
					return;
				}

				// Load the chunk containing the entity
				ChunkPos chunkPos = new ChunkPos(payload.entityPos());
				System.out.println("Loading chunk at: " + chunkPos);
				
				// Force load the chunk to FULL status
                world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

				// Wait a bit for the chunk to be processed, then try to find the entity
				context.client().execute(() -> {
					// Try to find the entity with retries
					attemptToFindAndSpectateEntity(context.client(), payload, 0);
				});
			});
		});

		// Register the payload type for S2C packets
        PayloadTypeRegistry.playS2C().register(DreamcastEntitySyncPayload.ID, DreamcastEntitySyncPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(DreamcastEntitySyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                MinecraftClient client = context.client();
                if (client.world == null) return;

                // Get the entity type
                EntityType<?> type = Registries.ENTITY_TYPE.get(payload.entityTypeId());
                if (type == null) {
                    System.out.println("Unknown entity type: " + payload.entityTypeId());
                    return;
                }

                // If we already have a dreamcasted entity, reuse it if type matches, else remove and create new
                if (dreamcastedEntity == null || !dreamcastedEntity.getType().equals(type)) {
                    if (dreamcastedEntity != null) client.world.removeEntity(dreamcastedEntity.getId(), Entity.RemovalReason.DISCARDED);
                    dreamcastedEntity = type.create(client.world);
                    if (dreamcastedEntity == null) {
                        System.out.println("Failed to create entity of type: " + payload.entityTypeId());
                        return;
                    }
                    // Set the entity ID to match the server (for camera tracking)
                    dreamcastedEntity.setId(payload.entityId());
                    client.world.addEntity(dreamcastedEntity);
                }

                // Update entity state
                dreamcastedEntity.setPos(payload.pos().getX() + 0.5, payload.pos().getY(), payload.pos().getZ() + 0.5);
                dreamcastedEntity.setYaw(payload.yaw());
                dreamcastedEntity.setPitch(payload.pitch());
                dreamcastedEntity.setVelocity(payload.velocity());
                dreamcastedEntity.readNbt(payload.nbt());

                // Set camera to spectate this entity only if not already
                if (client.cameraEntity != dreamcastedEntity) {
                    DreamcastingClient.spectateEntity(client, dreamcastedEntity);
                }
            });
        });

		JesusGui.init();
		HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter counter) -> {
			JesusGui.render (context);
		});

		// Register disconnect handler to clean up dreamcasting when disconnecting from server
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (DreamcastingClient.isDreamcasting()) {
				System.out.println("Cleaning up dreamcasting state due to disconnect");
				DreamcastingClient.dreamcast(client, false);
			}
		});

		// Register a tick event to lock the camera entity and perspective during dreamcasting
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (DreamcastingClient.isDreamcasting()) {
				DreamcastingClient.forceCameraLock(client);
			}
		});

		// Register client-side debug commands
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("dreamcastdebug")
				.executes(context -> {
					DreamcastingChunkManager.debugChunks();
					// For client commands, we just print to console since there's no chat to send to
					System.out.println("Dreamcasting debug info printed to console.");
					return 1;
				})
			);
		});

	}

	private void registerModMenuSound() {
		// In your main mod class or another appropriate place

	}

	private static void attemptToFindAndSpectateEntity(MinecraftClient client, EntityResponsePayload payload, int attempt) {
		if (attempt >= 10) { // Max 10 attempts
			System.out.println("Failed to find entity after " + attempt + " attempts. Giving up.");
			return;
		}

		client.execute(() -> {
			World world = client.world;
			if (world == null) return;

			// First try to find the entity in the world's entity list
			Entity requestedEntity = world.getEntityById(payload.entityId());
			
			if (requestedEntity != null) {
				System.out.println("Successfully found entity on attempt " + (attempt + 1) + ": " + requestedEntity.getName().getString());
				DreamcastingClient.spectateEntity(client, requestedEntity);
				return;
			}
			
			// If not found in world entity list, try to find it in the dreamcasting chunks
			if (DreamcastingChunkManager.isActive()) {
				ChunkPos entityChunkPos = new ChunkPos(payload.entityPos());
				
				if (DreamcastingChunkManager.hasChunkAtPosition(entityChunkPos.x, entityChunkPos.z)) {
					System.out.println("Found dreamcasting chunk at " + entityChunkPos.x + ", " + entityChunkPos.z + ", attempting direct spectating...");
					
					// Try direct spectating using the entity position from the payload
					// This bypasses the need to find the entity in the world's entity list
					DreamcastingClient.spectateEntityAtPosition(client, payload.entityPos(), payload.entityName());
					return;
				} else {
					System.out.println("Entity chunk not found in dreamcasting manager: " + entityChunkPos.x + ", " + entityChunkPos.z);
				}
			}
			
			System.out.println("Attempt " + (attempt + 1) + ": Entity with ID " + payload.entityId() + " not found, retrying...");
			
			// Wait a bit before retrying
			client.execute(() -> {
				attemptToFindAndSpectateEntity(client, payload, attempt + 1);
			});
		});
	}

}
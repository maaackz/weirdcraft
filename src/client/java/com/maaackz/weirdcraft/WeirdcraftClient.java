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
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.world.ClientWorld;

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
    
    // Getter for dreamcasted entity
    public static Entity getDreamcastedEntity() {
        return dreamcastedEntity;
    }

	private KeyBinding manualChunkClearKey;

	// Track previous sleeping state for sleep transition detection
	private boolean prevDreamcastedSleeping = false;
	private FakeCameraPlayerEntity fakeCameraPlayerEntity = null;

	private static int fakeCameraWaitTicks = 0;
	private static int lastCameraWaitEntityId = -1;


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
				System.out.println("[DEBUG] SleepPayload received, isSleeping=" + isSleeping);
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
                System.out.println("[DEBUG] Entity sync packet received, isDreamcasting=" + DreamcastingClient.isDreamcasting());
                if (!DreamcastingClient.isDreamcasting()) {
                    System.out.println("[DEBUG] Ignoring entity sync packet because not dreamcasting.");
                    return;
                }
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

                // --- Make the entity invisible and disable particles ---
                dreamcastedEntity.setInvisible(true);
                if (dreamcastedEntity instanceof net.minecraft.entity.LivingEntity living) {
                    living.setInvisible(true);
                    living.setSilent(false); // Optional: mute sounds
                    // TODO: Hide viewmodel/hand when spectating (requires mixin on HeldItemRenderer)
                }
                // Update entity state
                dreamcastedEntity.setPos(payload.pos().getX() + 0.5, payload.pos().getY(), payload.pos().getZ() + 0.5);
                dreamcastedEntity.setYaw(payload.yaw());
                dreamcastedEntity.setPitch(payload.pitch());
                dreamcastedEntity.setVelocity(payload.velocity());
                // System.out.println("[DREAMCAST CLIENT] NBT: " + payload.nbt());
                dreamcastedEntity.readNbt(payload.nbt());

                // --- Ensure camera follows entity rotation every tick ---
                if (client.cameraEntity == dreamcastedEntity) {
                    assert client.cameraEntity != null;
                    client.cameraEntity.setYaw(payload.yaw());
                    client.cameraEntity.setPitch(payload.pitch());
                }

                // --- Debug log fake entity properties ---
                System.out.println("[Dreamcast Debug] Entity type: " + dreamcastedEntity.getType());
                System.out.println("[Dreamcast Debug] Entity class: " + dreamcastedEntity.getClass().getName());
                System.out.println("[Dreamcast Debug] Position: " + dreamcastedEntity.getPos());
                System.out.println("[Dreamcast Debug] Yaw: " + dreamcastedEntity.getYaw() + ", Pitch: " + dreamcastedEntity.getPitch());
                System.out.println("[Dreamcast Debug] Velocity: " + dreamcastedEntity.getVelocity());
                if (dreamcastedEntity instanceof net.minecraft.entity.LivingEntity living) {
                    try {
                        Object pose = living.getClass().getMethod("getPose").invoke(living);
                        System.out.println("[Dreamcast Debug] Pose: " + pose);
                    } catch (Exception e) {
                        System.out.println("[Dreamcast Debug] Could not get pose: " + e);
                    }
                    try {
                        boolean isSleeping = (boolean) living.getClass().getMethod("isSleeping").invoke(living);
                        System.out.println("[Dreamcast Debug] isSleeping: " + isSleeping);
                    } catch (Exception e) {
                        System.out.println("[Dreamcast Debug] Could not get isSleeping: " + e);
                    }
                    // Print tracked data
                    try {
                        java.lang.reflect.Field dataTrackerField = net.minecraft.entity.LivingEntity.class.getDeclaredField("dataTracker");
                        dataTrackerField.setAccessible(true);
                        net.minecraft.entity.data.DataTracker tracker = (net.minecraft.entity.data.DataTracker) dataTrackerField.get(living);
                        System.out.println("[Dreamcast Debug] DataTracker: " + tracker);
                        // Print all tracked entries
                        try {
                            java.util.List entries = (java.util.List) tracker.getClass().getMethod("getEntries").invoke(tracker);
                            System.out.println("[Dreamcast Debug] Tracked entries: " + entries);
                        } catch (Exception e2) {
                            System.out.println("[Dreamcast Debug] Could not get tracked entries: " + e2);
                        }
                    } catch (Exception e) {
                        System.out.println("[Dreamcast Debug] Could not get DataTracker: " + e);
                    }
                }

                // --- Debug: Print all fields for this entity and its superclasses ---
                Class<?> debugClazz = dreamcastedEntity.getClass();
                // while (debugClazz != null) {
                //     for (java.lang.reflect.Field f : debugClazz.getDeclaredFields()) {
                //         System.out.println("[Dreamcast Debug] Field: " + f.getName() + " Type: " + f.getType().getName());
                //     }
                //     debugClazz = debugClazz.getSuperclass();
                // }

                // --- Animate and fix pose for correct entity types ---
                if (dreamcastedEntity instanceof net.minecraft.entity.LivingEntity living) {
                    // --- Animate walking ---
                    boolean isSleeping = false;
                    // --- Robust isSleeping calculation ---
                    if (dreamcastedEntity instanceof net.minecraft.entity.passive.VillagerEntity) {
                        // Use NBT for villager sleep state
                        if (payload.nbt().contains("Brain")) {
                            net.minecraft.nbt.NbtCompound brain = payload.nbt().getCompound("Brain");
                            if (brain.contains("memories")) {
                                net.minecraft.nbt.NbtCompound memories = brain.getCompound("memories");
                                long lastSlept = 0L, lastWoken = 0L;
                                if (memories.contains("minecraft:last_slept")) {
                                    lastSlept = memories.getCompound("minecraft:last_slept").getLong("value");
                                }
                                if (memories.contains("minecraft:last_woken")) {
                                    lastWoken = memories.getCompound("minecraft:last_woken").getLong("value");
                                }
                                isSleeping = lastSlept > lastWoken;
                                System.out.println("[Dreamcast Debug] Villager lastSlept=" + lastSlept + ", lastWoken=" + lastWoken + ", isSleeping=" + isSleeping);
                            }
                        }
                    } else {
                        // Use isSleeping() for other entities
                        try {
                            isSleeping = (boolean) living.getClass().getMethod("isSleeping").invoke(living);
                        } catch (Exception ignored) {
                            // Fallback: check pose
                            try {
                                Object pose = living.getClass().getMethod("getPose").invoke(living);
                                isSleeping = pose != null && pose.toString().equals("SLEEPING");
                            } catch (Exception ignored2) {}
                        }
                    }
                    System.out.println("[Dreamcast Debug] isSleeping: " + isSleeping);

                    // --- Forcibly set pose tracked data for Player/Villager if not sleeping ---
                    if ((dreamcastedEntity instanceof net.minecraft.entity.player.PlayerEntity || dreamcastedEntity instanceof net.minecraft.entity.passive.VillagerEntity) && !isSleeping) {
                        try {
                            // Find static TrackedData<EntityPose> field (POSE) by type
                            java.lang.reflect.Field poseField = null;
                            Class<?> clazz = living.getClass();
                            while (clazz != null && poseField == null) {
                                for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
                                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) &&
                                        net.minecraft.entity.data.TrackedData.class.isAssignableFrom(f.getType()) &&
                                        f.getGenericType().getTypeName().contains("EntityPose")) {
                                        poseField = f;
                                        break;
                                    }
                                }
                                clazz = clazz.getSuperclass();
                            }
                            if (poseField != null) {
                                poseField.setAccessible(true);
                                net.minecraft.entity.data.TrackedData trackedPose = (net.minecraft.entity.data.TrackedData) poseField.get(null);
                                living.getDataTracker().set(trackedPose, net.minecraft.entity.EntityPose.STANDING);
                                System.out.println("[Dreamcast Debug] Forcibly set pose tracked data to STANDING");
				} else {
                                System.out.println("[Dreamcast Debug] Could not find POSE tracked data field");
                            }
                        } catch (Exception e) {
                            System.out.println("[Dreamcast Debug] Could not forcibly set pose tracked data: " + e);
                        }
                    }

                    // Animate limb swing for all living entities
                    double dx = living.getX() - living.prevX;
                    double dz = living.getZ() - living.prevZ;
                    float limbSwingAmount = (float)Math.sqrt(dx * dx + dz * dz) * 4.0F;
                    boolean set = false;
                    try {
                        java.lang.reflect.Field f = living.getClass().getDeclaredField("limbDistance");
                        f.setAccessible(true);
                        f.setFloat(living, limbSwingAmount);
                        set = true;
                    } catch (Exception ignored) {}
                    if (!set) {
                        try {
                            java.lang.reflect.Field f = living.getClass().getDeclaredField("limbSwingAmount");
                            f.setAccessible(true);
                            f.setFloat(living, limbSwingAmount);
                        } catch (Exception ignored) {}
                    }
                }

                // Set camera to spectate this entity only if not already
                // if (client.cameraEntity != dreamcastedEntity) {
                    DreamcastingClient.spectateEntity(client, dreamcastedEntity);
				// }
			});
		});

//		ClientTickEvents.END_CLIENT_TICK.register(client -> {
//			if (manualChunkClearKey.wasPressed()) {
//				System.out.println("[Dreamcasting] F9 pressed: manual chunk clear/reload test");
//				com.maaackz.weirdcraft.DreamcastingClient.manualChunkClearTest();
//			}
//		});

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
				// --- Camera entity management ---
				if (dreamcastedEntity != null) {
					ChunkPos chunkPos = new ChunkPos(dreamcastedEntity.getBlockPos());
					boolean chunkLoaded = DreamcastingChunkManager.hasChunkAtPosition(chunkPos.x, chunkPos.z);
                    // Track how many ticks we've been waiting for this entity's chunk
                    if (fakeCameraPlayerEntity == null) {
                        fakeCameraWaitTicks = 0;
                        lastCameraWaitEntityId = dreamcastedEntity.getId();
                    } else if (lastCameraWaitEntityId != dreamcastedEntity.getId()) {
                        fakeCameraWaitTicks = 0;
                        lastCameraWaitEntityId = dreamcastedEntity.getId();
                    }
                    if (!chunkLoaded) {
                        fakeCameraWaitTicks++;
                        System.out.println("[FakeCamera] Waiting for chunk to load before switching camera (" + chunkPos.x + ", " + chunkPos.z + ")");
                        // Print all loaded dreamcasting chunks
                        System.out.print("[FakeCamera] DreamcastingChunkManager loaded chunks: ");
                        for (int[] c : DreamcastingChunkManager.getAllChunkCoords()) {
                            System.out.print("(" + c[0] + "," + c[1] + ") ");
                        }
                        System.out.println();
                        // Fallback: if entity is present and alive for >10 ticks, allow camera switch
                        if (fakeCameraWaitTicks < 10) {
                            // Do not switch camera yet
                            return;
                        } else if (dreamcastedEntity.isAlive()) {
                            System.out.println("[FakeCamera] Fallback: entity is alive after 10 ticks, switching camera anyway.");
                        } else {
                            return;
                        }
                    } else {
                        fakeCameraWaitTicks = 0;
                    }
					if (fakeCameraPlayerEntity == null || !fakeCameraPlayerEntity.isAlive() || fakeCameraPlayerEntity.getWorld() != client.world) {
						// Remove any old camera entity
						if (fakeCameraPlayerEntity != null && fakeCameraPlayerEntity.getWorld() != null) {
							fakeCameraPlayerEntity.discard();
						}
						// Create new fake player camera entity
						fakeCameraPlayerEntity = new FakeCameraPlayerEntity((ClientWorld)client.world, client.getNetworkHandler(), client.player);
						fakeCameraPlayerEntity.setTargetEntity(dreamcastedEntity);
						client.world.spawnEntity(fakeCameraPlayerEntity);
						System.out.println("[FakeCamera] Created and spawned new fake player camera entity");
					}
					// Always sync target entity
					fakeCameraPlayerEntity.setTargetEntity(dreamcastedEntity);
					// --- Forcibly sync rotation every tick ---
					if (fakeCameraPlayerEntity != null && dreamcastedEntity != null) {
						fakeCameraPlayerEntity.setYaw(dreamcastedEntity.getYaw());
						fakeCameraPlayerEntity.setPitch(dreamcastedEntity.getPitch());
						fakeCameraPlayerEntity.prevYaw = dreamcastedEntity.prevYaw;
						fakeCameraPlayerEntity.prevPitch = dreamcastedEntity.prevPitch;
					}
					// Force camera lock
					if (client.cameraEntity != fakeCameraPlayerEntity) {
						System.out.println("[FakeCamera] Switching camera entity. Camera: " + System.identityHashCode(fakeCameraPlayerEntity) + ", Spectated: " + System.identityHashCode(dreamcastedEntity));
						client.setCameraEntity(fakeCameraPlayerEntity);
						System.out.println("[FakeCamera] Set camera entity to fake player camera");
					}
				}

				// Print debug info every second
				if (System.currentTimeMillis() % 1000 < 50) {
					System.out.println("[DEBUG] Camera entity: " + client.cameraEntity + " (" + (client.cameraEntity != null ? client.cameraEntity.getClass().getName() : "null") + ")");
					System.out.println("[DEBUG] Dreamcasted entity: " + (dreamcastedEntity != null ? dreamcastedEntity + " (" + dreamcastedEntity.getClass().getName() + ")" : "null"));
					if (client.cameraEntity != null) {
						System.out.println("[DEBUG] Camera yaw: " + client.cameraEntity.getYaw() + ", pitch: " + client.cameraEntity.getPitch());
					}
					if (dreamcastedEntity != null) {
						System.out.println("[DEBUG] Dreamcasted entity yaw: " + dreamcastedEntity.getYaw() + ", pitch: " + dreamcastedEntity.getPitch());
					}
					Camera camera = client.gameRenderer.getCamera();
					System.out.println("[DEBUG] Camera object reference: " + camera);
				}

				// --- Sleep transition detection and handling ---
				boolean isSleeping = false;
				if (dreamcastedEntity != null) {
					try {
						isSleeping = (boolean) dreamcastedEntity.getClass().getMethod("isSleeping").invoke(dreamcastedEntity);
					} catch (Exception ignored) {}
				}
				if (prevDreamcastedSleeping && !isSleeping) {
					System.out.println("[DEBUG] Detected sleep -> wake transition for dreamcasted entity!");
					// 1. Forcibly toggle perspective
					if (client.options.getPerspective() != net.minecraft.client.option.Perspective.THIRD_PERSON_BACK) {
						client.options.setPerspective(net.minecraft.client.option.Perspective.THIRD_PERSON_BACK);
						System.out.println("[DEBUG] Toggled to third person perspective after sleep");
					}
					client.options.setPerspective(net.minecraft.client.option.Perspective.FIRST_PERSON);
					System.out.println("[DEBUG] Toggled back to first person perspective after sleep");
					// 2. Re-create fake player camera entity after sleep
					if (fakeCameraPlayerEntity != null && fakeCameraPlayerEntity.getWorld() != null) {
						fakeCameraPlayerEntity.discard();
					}
					fakeCameraPlayerEntity = new FakeCameraPlayerEntity((ClientWorld)client.world, client.getNetworkHandler(), client.player);
					fakeCameraPlayerEntity.setTargetEntity(dreamcastedEntity);
					client.world.spawnEntity(fakeCameraPlayerEntity);
					System.out.println("[FakeCamera] Re-created fake player camera entity after sleep. Camera: " + System.identityHashCode(fakeCameraPlayerEntity) + ", Spectated: " + System.identityHashCode(dreamcastedEntity));
					client.setCameraEntity(fakeCameraPlayerEntity);
				}
				prevDreamcastedSleeping = isSleeping;
			}
			// Failsafe: forcibly return camera to player if needed
			DreamcastingClient.clientTickFailsafe(client);
		});

		// Register client-side debug commands
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("dreamcastdebug")
				.executes(context -> {
					DreamcastingChunkManager.debugChunks();
					System.out.println("Dreamcasting debug info printed to console.");
					return 1;
				})
			);
            dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("chunkfields")
                .executes(context -> {
                    System.out.println("--- ClientChunkManager fields ---");
                    java.lang.reflect.Field[] fields = net.minecraft.client.world.ClientChunkManager.class.getDeclaredFields();
                    for (java.lang.reflect.Field f : fields) {
                        f.setAccessible(true);
                        System.out.println("Field: " + f.getName() + " Type: " + f.getType().getName());
                    }
                    System.out.println("--- END ---");
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
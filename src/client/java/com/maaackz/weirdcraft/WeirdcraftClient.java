package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.datagen.CustomEntities;
import com.maaackz.weirdcraft.item.custom.PocketWatchItem;
import com.maaackz.weirdcraft.item.custom.RainesCloudItem;
import com.maaackz.weirdcraft.network.*;
import com.maaackz.weirdcraft.renderer.HolyMackerelRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
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
				System.out.println("Entity response received.");

				// Ensure the chunk containing the entity is loaded
				Entity requestedEntity = null;
				World world = context.client().world;

				// Try loading the chunk containing the entity
				ChunkPos chunkPos = new ChunkPos(new BlockPos(payload.entityPos())); // assuming entityPos is a Vec3d
                assert world != null;
                world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

				// Now try to get the entity by ID
				requestedEntity = world.getEntityById(payload.entityId());

				if (requestedEntity != null) {
					// Optionally, do something with the entity, like spectating it
					System.out.println("Received entity data: Name=" + payload.entityName() + ", Position=" + payload.entityPos() + ", ID=" + payload.entityId());
					DreamcastingClient.spectateEntity(context.client(),requestedEntity);
				} else {
					System.out.println("Entity with ID " + payload.entityId() + " is not loaded.");
				}
			});
		});

		JesusGui.init();
		HudRenderCallback.EVENT.register((DrawContext context, RenderTickCounter counter) -> {
			JesusGui.render (context);
		});

	}

	private void registerModMenuSound() {
		// In your main mod class or another appropriate place

	}

}
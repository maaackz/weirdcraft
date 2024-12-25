package com.maaackz.weirdcraft;

import com.maaackz.weirdcraft.item.custom.PocketWatchItem;
import com.maaackz.weirdcraft.network.SleepPayload;
import com.maaackz.weirdcraft.network.TimePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class WeirdcraftClient implements ClientModInitializer {

	private static final KeyBinding advanceKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.weirdcraft.advance_time",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_Z, // Default key: Z
			"category.weirdcraft.keys"
	));
	private static final KeyBinding reverseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.weirdcraft.reverse_time",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_X, // Default key: X
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



		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.world == null) return;

			boolean holdingPocketWatch = client.player.getMainHandStack().getItem() instanceof PocketWatchItem;

			if (holdingPocketWatch) {
				if (advanceKey.isPressed()) {
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

				if (reverseKey.isPressed()) {
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
					ClientDreamcasting.dreamcast(context.client(), true);  // You may pass additional data as needed
				} else {
					// Handle the stop of the sleep
					ClientDreamcasting.dreamcast(context.client(), false);  // Similarly, handle stop logic
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